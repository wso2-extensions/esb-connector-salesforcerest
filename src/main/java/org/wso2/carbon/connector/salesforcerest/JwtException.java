/*
 * Copyright (c) 2026, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.connector.salesforcerest;

/**
 * Error of the JWT bearer flow, with the ERROR_CODE set on the message. Messages never contain keys, passwords,
 * assertions or tokens.
 */
final class JwtException extends Exception {

    /** Invalid configuration or signing key. */
    static final String ERROR_CONFIG = "SF_JWT_CONFIG";
    /** The token endpoint rejected the request, returned an unusable response, or could not be reached. */
    static final String ERROR_TOKEN = "SF_JWT_TOKEN";

    private static final long serialVersionUID = 1L;
    private final String code;

    JwtException(String code, String message) {
        super(message);
        this.code = code;
    }

    String getCode() {
        return code;
    }
}
