/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Project  : EasyAvatar
 * Package  : net.solosky.easyavatar.api
 * File     : Error.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2011-10-24
 * License  : Apache License 2.0 
 */
package com.cocosw.framework.exception;

/**
 * 错误信息枚举
 * 
 * @author solosky <solosky772@qq.com>
 */
public enum ErrorCode {

	/**
	 * 网络连接问题
	 */
	NETWORK_ERROR,
	/**
	 * 没有错误/未知错误
	 */
	NO_ERROR,
	/**
	 * 无效的用户
	 */
	INVALID_USER,
	/**
	 * 密码错误
	 */
	WRONG_PASSWORD,
	/**
	 * 验证码错误
	 */
	WRONG_CAPTCHA,
	/**
	 * 需要验证
	 */
	NEED_CAPTCHA,
	/**
	 * 网络错误
	 */
	IO_ERROR,
	/**
	 * 无法解析的结果
	 */
	INVALID_RESPONSE,
	/**
	 * 头像不存在
	 */
	NO_AVATAR,
	/**
	 * 功能不支持
	 */
	NOT_SUPPORTED,
	/**
	 * 没有更多的朋友了
	 */
	NO_MORE_FIRENDS,
	/**
	 * 资源没有更改
	 */
	NOT_MODIFIED,
	/**
	 * 初始化错误
	 */
	INIT_ERROR,
	/**
	 * 用户取消操作
	 */
	CANCELED,

	// Token过期,会要求重新登录
	ACCESS_TOKEN_EXPIRED,

	/**
	 * 超过限额
	 */
	EXCCED_LIMIT,

	/**
	 * 没有权限
	 */
	NO_PERMISSION,

	/**
	 * 服务器端错误
	 */
	SERVER_ERROR,
}
