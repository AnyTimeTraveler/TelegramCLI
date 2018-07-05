/*
 * Copyright Simon Struck (tgcli@simonscode.org) 2018
 *
 * Distributed under the MIT License (See the file LICENSE.txt)
 *
 */
package org.simonscode.tgcli.telegram;

import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdClient;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MessageHandler {
    public final ConcurrentMap<Integer, TdApi.User> users = new ConcurrentHashMap<>();
    public final ConcurrentMap<Long, TdApi.Chat> chats = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, TdApi.BasicGroup> basicGroups = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, TdApi.Supergroup> supergroups = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, TdApi.SecretChat> secretChats = new ConcurrentHashMap<>();
    private final NavigableSet<OrderedChat> chatList = new TreeSet<>();
    private final ConcurrentMap<Integer, TdApi.UserFullInfo> usersFullInfo = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, TdApi.BasicGroupFullInfo> basicGroupsFullInfo = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, TdApi.SupergroupFullInfo> supergroupsFullInfo = new ConcurrentHashMap<>();
    public TdApi.AuthorizationState authorizationState = null;
    private TdClient nativeClient;
    private boolean haveFullChatList = false;

    MessageHandler(TdClient nativeClient) {
        this.nativeClient = nativeClient;
    }

    @Deprecated
    public void getChatList(final int limit) {
        synchronized (chatList) {
            if (!haveFullChatList && limit > chatList.size()) {
                // have enough chats in the chat list or chat list is too small
                long offsetOrder = Long.MAX_VALUE;
                long offsetChatId = 0;
                if (!chatList.isEmpty()) {
                    OrderedChat last = chatList.last();
                    offsetOrder = last.order;
                    offsetChatId = last.chatId;
                }
                nativeClient.send(new TdApi.GetChats(offsetOrder, offsetChatId, limit - chatList.size()), object -> {
                    switch (object.getConstructor()) {
                        case TdApi.Error.CONSTRUCTOR:
                            System.err.println("Receive an error for GetChats:");
                            System.err.println(object);
                            break;
                        case TdApi.Chats.CONSTRUCTOR:
                            long[] chatIds = ((TdApi.Chats) object).chatIds;
                            if (chatIds.length == 0) {
                                synchronized (chatList) {
                                    haveFullChatList = true;
                                }
                            }
                            // chats had already been received through updates, let's retry request
                            getChatList(limit);
                            break;
                        default:
                            System.err.println("Receive wrong response from TDLib:");
                            System.err.println(object);
                    }
                });
                return;
            }

            // have enough chats in the chat list to answer request
            java.util.Iterator<OrderedChat> iter = chatList.iterator();
            System.out.println();
            System.out.println("First " + limit + " chat(s) out of " + chatList.size() + " known chat(s):");
            for (int i = 0; i < limit; i++) {
                long chatId = iter.next().chatId;
                TdApi.Chat chat = chats.get(chatId);
                System.out.println(chatId + ": " + chat.title);
            }
            System.out.println();
        }
    }

    public void handle(TdApi.Object object) {
        switch (object.getConstructor()) {
            case TdApi.UpdateUser.CONSTRUCTOR:
                TdApi.UpdateUser updateUser = (TdApi.UpdateUser) object;
                users.put(updateUser.user.id, updateUser.user);
                break;
            case TdApi.UpdateUserStatus.CONSTRUCTOR: {
                TdApi.UpdateUserStatus updateUserStatus = (TdApi.UpdateUserStatus) object;
                TdApi.User user = users.get(updateUserStatus.userId);
                synchronized (user) {
                    user.status = updateUserStatus.status;
                }
                break;
            }
            case TdApi.UpdateBasicGroup.CONSTRUCTOR:
                TdApi.UpdateBasicGroup updateBasicGroup = (TdApi.UpdateBasicGroup) object;
                basicGroups.put(updateBasicGroup.basicGroup.id, updateBasicGroup.basicGroup);
                break;
            case TdApi.UpdateSupergroup.CONSTRUCTOR:
                TdApi.UpdateSupergroup updateSupergroup = (TdApi.UpdateSupergroup) object;
                supergroups.put(updateSupergroup.supergroup.id, updateSupergroup.supergroup);
                break;
            case TdApi.UpdateSecretChat.CONSTRUCTOR:
                TdApi.UpdateSecretChat updateSecretChat = (TdApi.UpdateSecretChat) object;
                secretChats.put(updateSecretChat.secretChat.id, updateSecretChat.secretChat);
                break;

            case TdApi.UpdateNewChat.CONSTRUCTOR: {
                TdApi.UpdateNewChat updateNewChat = (TdApi.UpdateNewChat) object;
                TdApi.Chat chat = updateNewChat.chat;
                synchronized (chat) {
                    chats.put(chat.id, chat);

                    long order = chat.order;
                    chat.order = 0;
                    setChatOrder(chat, order);
                }
                break;
            }
            case TdApi.UpdateChatTitle.CONSTRUCTOR: {
                TdApi.UpdateChatTitle updateChat = (TdApi.UpdateChatTitle) object;
                TdApi.Chat chat = chats.get(updateChat.chatId);
                synchronized (chat) {
                    chat.title = updateChat.title;
                }
                break;
            }
            case TdApi.UpdateChatPhoto.CONSTRUCTOR: {
                TdApi.UpdateChatPhoto updateChat = (TdApi.UpdateChatPhoto) object;
                TdApi.Chat chat = chats.get(updateChat.chatId);
                synchronized (chat) {
                    chat.photo = updateChat.photo;
                }
                break;
            }
            case TdApi.UpdateChatLastMessage.CONSTRUCTOR: {
                TdApi.UpdateChatLastMessage updateChat = (TdApi.UpdateChatLastMessage) object;
                TdApi.Chat chat = chats.get(updateChat.chatId);
                synchronized (chat) {
                    chat.lastMessage = updateChat.lastMessage;
                    setChatOrder(chat, updateChat.order);
                }
                break;
            }
            case TdApi.UpdateChatOrder.CONSTRUCTOR: {
                TdApi.UpdateChatOrder updateChat = (TdApi.UpdateChatOrder) object;
                TdApi.Chat chat = chats.get(updateChat.chatId);
                synchronized (chat) {
                    setChatOrder(chat, updateChat.order);
                }
                break;
            }
            case TdApi.UpdateChatIsPinned.CONSTRUCTOR: {
                TdApi.UpdateChatIsPinned updateChat = (TdApi.UpdateChatIsPinned) object;
                TdApi.Chat chat = chats.get(updateChat.chatId);
                synchronized (chat) {
                    chat.isPinned = updateChat.isPinned;
                    setChatOrder(chat, updateChat.order);
                }
                break;
            }
            case TdApi.UpdateChatReadInbox.CONSTRUCTOR: {
                TdApi.UpdateChatReadInbox updateChat = (TdApi.UpdateChatReadInbox) object;
                TdApi.Chat chat = chats.get(updateChat.chatId);
                synchronized (chat) {
                    chat.lastReadInboxMessageId = updateChat.lastReadInboxMessageId;
                    chat.unreadCount = updateChat.unreadCount;
                }
                break;
            }
            case TdApi.UpdateChatReadOutbox.CONSTRUCTOR: {
                TdApi.UpdateChatReadOutbox updateChat = (TdApi.UpdateChatReadOutbox) object;
                TdApi.Chat chat = chats.get(updateChat.chatId);
                synchronized (chat) {
                    chat.lastReadOutboxMessageId = updateChat.lastReadOutboxMessageId;
                }
                break;
            }
            case TdApi.UpdateChatUnreadMentionCount.CONSTRUCTOR: {
                TdApi.UpdateChatUnreadMentionCount updateChat = (TdApi.UpdateChatUnreadMentionCount) object;
                TdApi.Chat chat = chats.get(updateChat.chatId);
                synchronized (chat) {
                    chat.unreadMentionCount = updateChat.unreadMentionCount;
                }
                break;
            }
            case TdApi.UpdateMessageMentionRead.CONSTRUCTOR: {
                TdApi.UpdateMessageMentionRead updateChat = (TdApi.UpdateMessageMentionRead) object;
                TdApi.Chat chat = chats.get(updateChat.chatId);
                synchronized (chat) {
                    chat.unreadMentionCount = updateChat.unreadMentionCount;
                }
                break;
            }
            case TdApi.UpdateChatReplyMarkup.CONSTRUCTOR: {
                TdApi.UpdateChatReplyMarkup updateChat = (TdApi.UpdateChatReplyMarkup) object;
                TdApi.Chat chat = chats.get(updateChat.chatId);
                synchronized (chat) {
                    chat.replyMarkupMessageId = updateChat.replyMarkupMessageId;
                }
                break;
            }
            case TdApi.UpdateChatDraftMessage.CONSTRUCTOR: {
                TdApi.UpdateChatDraftMessage updateChat = (TdApi.UpdateChatDraftMessage) object;
                TdApi.Chat chat = chats.get(updateChat.chatId);
                synchronized (chat) {
                    chat.draftMessage = updateChat.draftMessage;
                    setChatOrder(chat, updateChat.order);
                }
                break;
            }
            case TdApi.UpdateNotificationSettings.CONSTRUCTOR: {
                TdApi.UpdateNotificationSettings update = (TdApi.UpdateNotificationSettings) object;
                if (update.scope instanceof TdApi.NotificationSettingsScopeChat) {
                    TdApi.Chat chat = chats.get(((TdApi.NotificationSettingsScopeChat) update.scope).chatId);
                    synchronized (chat) {
                        chat.notificationSettings = update.notificationSettings;
                    }
                }
                break;
            }

            case TdApi.UpdateUserFullInfo.CONSTRUCTOR:
                TdApi.UpdateUserFullInfo updateUserFullInfo = (TdApi.UpdateUserFullInfo) object;
                usersFullInfo.put(updateUserFullInfo.userId, updateUserFullInfo.userFullInfo);
                break;
            case TdApi.UpdateBasicGroupFullInfo.CONSTRUCTOR:
                TdApi.UpdateBasicGroupFullInfo updateBasicGroupFullInfo = (TdApi.UpdateBasicGroupFullInfo) object;
                basicGroupsFullInfo.put(updateBasicGroupFullInfo.basicGroupId, updateBasicGroupFullInfo.basicGroupFullInfo);
                break;
            case TdApi.UpdateSupergroupFullInfo.CONSTRUCTOR:
                TdApi.UpdateSupergroupFullInfo updateSupergroupFullInfo = (TdApi.UpdateSupergroupFullInfo) object;
                supergroupsFullInfo.put(updateSupergroupFullInfo.supergroupId, updateSupergroupFullInfo.supergroupFullInfo);
                break;
            default:
                // print("Unsupported update:" + newLine + object);
        }
    }

    private void setChatOrder(TdApi.Chat chat, long order) {
        synchronized (chatList) {
            if (chat.order != 0) {
                boolean isRemoved = chatList.remove(new OrderedChat(chat.order, chat.id));
                assert isRemoved;
            }

            chat.order = order;

            if (chat.order != 0) {
                boolean isAdded = chatList.add(new OrderedChat(chat.order, chat.id));
                assert isAdded;
            }
        }
    }

}