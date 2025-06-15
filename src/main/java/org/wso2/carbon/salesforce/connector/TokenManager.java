/*
 *  Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.salesforce.connector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TokenManager {
    private static final Log log = LogFactory.getLog(TokenManager.class);
    private static final TokenStore TOKEN_STORE = new InMemoryTokenStore();

    private TokenManager() {
    }

    public static void addToken(String resourceKey, Token token) {
        TOKEN_STORE.add(resourceKey, token);
    }

    public static Token getToken(String resourceKey) {
        return TOKEN_STORE.get(resourceKey);
    }

    public static void removeToken(String resourceKey) {
        TOKEN_STORE.remove(resourceKey);
    }

    public static void clean() {
        TOKEN_STORE.clean();
        if (log.isDebugEnabled()) {
            log.debug("Token map cleaned.");
        }
    }
}
