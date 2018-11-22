/*
 * Copyright (c) 2012-2013, Poplar Yfyang 杨友峰 (poplar1123@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flagwind.mybatis.plugins.paginator.helpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.flagwind.mybatis.plugins.paginator.DefaultParameterHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.transaction.Transaction;

import com.flagwind.mybatis.plugins.paginator.dialects.Dialect;

/**
 * @author poplar.yfyang
 * @author miemiedev
 */
public class SQLHelper {
    private static Log LOG = LogFactory.getLog(SQLHelper.class);

    /**
     * @Title: getCount
     * @Description: 查询总纪录数
     * @return int 返回类型
     */
    public static int getCount(final MappedStatement mappedStatement, final Transaction transaction,
                               final Object parameterObject, final BoundSql boundSql, Dialect dialect) throws SQLException {
        final String count_sql = dialect.getCountSQL();
        LOG.debug(String.format("Total count SQL [{}] ", count_sql));
        LOG.debug(String.format("Total count Parameters: {} ", parameterObject));

        Connection connection = null;

        try {
            connection = transaction.getConnection();
            PreparedStatement countStmt = connection.prepareStatement(count_sql);
            DefaultParameterHandler handler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
            handler.setParameters(countStmt);

            ResultSet rs = countStmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            LOG.debug(String.format("Total count: {}", count));
            return count;
        }
        finally {
            if (connection != null) {
                //connection.close();
            }
        }

    }

}