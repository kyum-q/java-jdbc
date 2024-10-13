package com.techcourse.service;

import com.techcourse.dao.UserDao;
import com.techcourse.dao.UserHistoryDao;
import com.techcourse.domain.User;
import com.techcourse.domain.UserHistory;
import com.techcourse.service.transaction.TransactionManager;

public class UserService {

    private final UserDao userDao;
    private final UserHistoryDao userHistoryDao;

    public UserService(final UserDao userDao, final UserHistoryDao userHistoryDao) {
        this.userDao = userDao;
        this.userHistoryDao = userHistoryDao;
    }

    public User findById(final long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 id인 user가 존재하지 않습니다."));
    }

    public void insert(final User user) {
        TransactionManager.runTransaction((connection) -> userDao.insert(connection, user));
    }

    public void changePassword(final long id, final String newPassword, final String createBy) {
        final var user = findById(id);
        user.changePassword(newPassword);

        TransactionManager.runTransaction((connection) -> {
            userDao.update(connection, user);
            userHistoryDao.log(connection, new UserHistory(user, createBy));
        });
    }

    public User findByAccount(String account) {
        return userDao.findByAccount(account)
                .orElseThrow(() -> new IllegalArgumentException("해당 account인 user가 존재하지 않습니다."));
    }
}
