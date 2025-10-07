/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.sharepoint.methods;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.InstancePool;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.sharepoint.SharepointException;
import com.liferay.portal.sharepoint.SharepointRequest;

import java.util.Map;

/**
 * @author Bruno Farache
 */
public class MethodFactory {

	public static Method create(SharepointRequest sharepointRequest)
		throws SharepointException {

		return _methodFactory._create(sharepointRequest);
	}

	private MethodFactory() {
		Method method = (Method)InstancePool.get(_CHECKOUT_METHOD_IMPL);

		_methods = HashMapBuilder.<String, Object>put(
			method.getMethodName(), method
		).build();

		method = (Method)InstancePool.get(_CREATE_URL_DIRECTORIES_METHOD_IMPL);

		_methods.put(method.getMethodName(), method);

		method = (Method)InstancePool.get(_GET_DOCS_META_INFO_METHOD_IMPL);

		_methods.put(method.getMethodName(), method);

		method = (Method)InstancePool.get(_GET_DOCUMENT_METHOD_IMPL);

		_methods.put(method.getMethodName(), method);

		method = (Method)InstancePool.get(_LIST_DOCUMENTS_METHOD_IMPL);

		_methods.put(method.getMethodName(), method);

		method = (Method)InstancePool.get(_MOVE_DOCUMENT_METHOD_IMPL);

		_methods.put(method.getMethodName(), method);

		method = (Method)InstancePool.get(_OPEN_SERVICE_METHOD_IMPL);

		_methods.put(method.getMethodName(), method);

		method = (Method)InstancePool.get(_PUT_DOCUMENT_METHOD_IMPL);

		_methods.put(method.getMethodName(), method);

		method = (Method)InstancePool.get(_REMOVE_DOCUMENTS_METHOD_IMPL);

		_methods.put(method.getMethodName(), method);

		method = (Method)InstancePool.get(_SERVER_VERSION_METHOD_IMPL);

		_methods.put(method.getMethodName(), method);

		method = (Method)InstancePool.get(_UNCHECKOUT_DOCUMENT_METHOD_IMPL);

		_methods.put(method.getMethodName(), method);

		method = (Method)InstancePool.get(_URL_TO_WEB_URL_METHOD_IMPL);

		_methods.put(method.getMethodName(), method);
	}

	private Method _create(SharepointRequest sharepointRequest)
		throws SharepointException {

		String method = sharepointRequest.getParameterValue("method");

		method = method.split(StringPool.COLON)[0];

		Method methodImpl = (Method)_methods.get(method);

		if (methodImpl == null) {
			throw new SharepointException(
				"Method " + method + " is not implemented");
		}

		return methodImpl;
	}

	private static final String _CHECKOUT_METHOD_IMPL = GetterUtil.getString(
		PropsUtil.get(MethodFactory.class.getName() + ".CHECKOUT"),
		CheckoutMethodImpl.class.getName());

	private static final String _CREATE_URL_DIRECTORIES_METHOD_IMPL =
		GetterUtil.getString(
			PropsUtil.get(
				MethodFactory.class.getName() + ".CREATE_URL_DIRECTORIES"),
			CreateURLDirectoriesMethodImpl.class.getName());

	private static final String _GET_DOCS_META_INFO_METHOD_IMPL =
		GetterUtil.getString(
			PropsUtil.get(
				MethodFactory.class.getName() + ".GET_DOCS_META_INFO"),
			GetDocsMetaInfoMethodImpl.class.getName());

	private static final String _GET_DOCUMENT_METHOD_IMPL =
		GetterUtil.getString(
			PropsUtil.get(MethodFactory.class.getName() + ".GET_DOCUMENT"),
			GetDocumentMethodImpl.class.getName());

	private static final String _LIST_DOCUMENTS_METHOD_IMPL =
		GetterUtil.getString(
			PropsUtil.get(MethodFactory.class.getName() + ".LIST_DOCUMENTS"),
			ListDocumentsMethodImpl.class.getName());

	private static final String _MOVE_DOCUMENT_METHOD_IMPL =
		GetterUtil.getString(
			PropsUtil.get(MethodFactory.class.getName() + ".MOVE_DOCUMENT"),
			MoveDocumentMethodImpl.class.getName());

	private static final String _OPEN_SERVICE_METHOD_IMPL =
		GetterUtil.getString(
			PropsUtil.get(MethodFactory.class.getName() + ".OPEN_SERVICE"),
			OpenServiceMethodImpl.class.getName());

	private static final String _PUT_DOCUMENT_METHOD_IMPL =
		GetterUtil.getString(
			PropsUtil.get(MethodFactory.class.getName() + ".PUT_DOCUMENT"),
			PutDocumentMethodImpl.class.getName());

	private static final String _REMOVE_DOCUMENTS_METHOD_IMPL =
		GetterUtil.getString(
			PropsUtil.get(MethodFactory.class.getName() + ".REMOVE_DOCUMENTS"),
			RemoveDocumentsMethodImpl.class.getName());

	private static final String _SERVER_VERSION_METHOD_IMPL =
		GetterUtil.getString(
			PropsUtil.get(MethodFactory.class.getName() + ".SERVER_VERSION"),
			ServerVersionMethodImpl.class.getName());

	private static final String _UNCHECKOUT_DOCUMENT_METHOD_IMPL =
		GetterUtil.getString(
			PropsUtil.get(
				MethodFactory.class.getName() + ".UNCHECKOUT_DOCUMENT"),
			UncheckoutDocumentMethodImpl.class.getName());

	private static final String _URL_TO_WEB_URL_METHOD_IMPL =
		GetterUtil.getString(
			PropsUtil.get(MethodFactory.class.getName() + ".URL_TO_WEB_URL"),
			UrlToWebUrlMethodImpl.class.getName());

	private static final MethodFactory _methodFactory = new MethodFactory();

	private final Map<String, Object> _methods;

}