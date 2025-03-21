/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upload;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.io.ByteArrayFileInputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadException;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.upload.configuration.UploadServletRequestConfigurationProviderUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ProgressTracker;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Brian Wing Shun Chan
 * @author Zongliang Li
 * @author Harry Mark
 * @author Raymond Augé
 */
public class UploadServletRequestImpl
	extends HttpServletRequestWrapper implements UploadServletRequest {

	public UploadServletRequestImpl(HttpServletRequest httpServletRequest) {
		this(httpServletRequest, 0, null);
	}

	public UploadServletRequestImpl(
		HttpServletRequest httpServletRequest, int fileSizeThreshold,
		String location) {

		super(httpServletRequest);

		_fileParameters = new LinkedHashMap<>();
		_regularParameters = new LinkedHashMap<>();

		LiferayServletRequest liferayServletRequest = null;

		try {
			HttpSession httpSession = httpServletRequest.getSession();

			httpSession.removeAttribute(ProgressTracker.PERCENT);

			ServletFileUpload servletFileUpload =
				_servletFileUploadSnapshot.get();

			liferayServletRequest = new LiferayServletRequest(
				httpServletRequest);

			location = GetterUtil.getString(
				location,
				UploadServletRequestConfigurationProviderUtil.getTempDir());

			List<FileItem> fileItemsList = servletFileUpload.parseRequest(
				liferayServletRequest, location, fileSizeThreshold);

			liferayServletRequest.setFinishedReadingOriginalStream(true);

			int contentLength = httpServletRequest.getContentLength();
			long uploadServletRequestImplMaxSize =
				UploadServletRequestConfigurationProviderUtil.getMaxSize();
			long uploadServletRequestImplSize = 0;

			if ((uploadServletRequestImplMaxSize > 0) &&
				((contentLength == -1) ||
				 (contentLength > uploadServletRequestImplMaxSize))) {

				fileItemsList = _sort(fileItemsList);
			}

			for (FileItem fileItem : fileItemsList) {
				if (uploadServletRequestImplMaxSize > 0) {
					long itemSize = fileItem.getSize();

					if ((uploadServletRequestImplSize + itemSize) >
							uploadServletRequestImplMaxSize) {

						UploadException uploadException = new UploadException(
							StringBundler.concat(
								"Request reached the maximum permitted size ",
								"of ", uploadServletRequestImplMaxSize,
								" bytes"));

						uploadException.setExceededUploadRequestSizeLimit(true);

						httpServletRequest.setAttribute(
							WebKeys.UPLOAD_EXCEPTION, uploadException);

						continue;
					}

					uploadServletRequestImplSize += itemSize;
				}

				if (fileItem.isFormField()) {
					String fieldName = fileItem.getFieldName();

					if (!_regularParameters.containsKey(fieldName)) {
						_regularParameters.put(
							fieldName, new ArrayList<String>());
					}

					List<String> values = _regularParameters.get(fieldName);

					if (fileItem.getSize() > FileItem.THRESHOLD_SIZE) {
						UploadException uploadException = new UploadException(
							StringBundler.concat(
								"The field ", fieldName,
								" exceeds its maximum permitted size of ",
								FileItem.THRESHOLD_SIZE, " bytes"));

						uploadException.setExceededLiferayFileItemSizeLimit(
							true);

						httpServletRequest.setAttribute(
							WebKeys.UPLOAD_EXCEPTION, uploadException);
					}

					values.add(fileItem.getString());

					continue;
				}

				FileItem[] fileItems = _fileParameters.get(
					fileItem.getFieldName());

				if (fileItems == null) {
					fileItems = new FileItem[] {fileItem};
				}
				else {
					FileItem[] newFileItems =
						new FileItem[fileItems.length + 1];

					System.arraycopy(
						fileItems, 0, newFileItems, 0, fileItems.length);

					newFileItems[newFileItems.length - 1] = fileItem;

					fileItems = newFileItems;
				}

				_fileParameters.put(fileItem.getFieldName(), fileItems);
			}
		}
		catch (UploadException uploadException) {
			httpServletRequest.setAttribute(
				WebKeys.UPLOAD_EXCEPTION, uploadException);

			Throwable throwable = uploadException.getCause();

			if (_log.isDebugEnabled()) {
				_log.debug(throwable);
			}
			else if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to parse upload request: " +
						throwable.getMessage());
			}
		}

		_liferayServletRequest = liferayServletRequest;
	}

	public UploadServletRequestImpl(
		HttpServletRequest httpServletRequest,
		Map<String, FileItem[]> fileParameters,
		Map<String, List<String>> regularParameters) {

		super(httpServletRequest);

		_fileParameters = new LinkedHashMap<>();
		_regularParameters = new LinkedHashMap<>();

		if (fileParameters != null) {
			_fileParameters.putAll(fileParameters);
		}

		if (regularParameters != null) {
			_regularParameters.putAll(regularParameters);
		}

		_liferayServletRequest = null;
	}

	@Override
	public void cleanUp() {
		if ((_fileParameters != null) && !_fileParameters.isEmpty()) {
			for (FileItem[] liferayFileItems : _fileParameters.values()) {
				for (FileItem liferayFileItem : liferayFileItems) {
					liferayFileItem.delete();
				}
			}
		}

		if (_liferayServletRequest != null) {
			_liferayServletRequest.cleanUp();
		}
	}

	@Override
	public String getContentType(String name) {
		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			FileItem liferayFileItem = liferayFileItems[0];

			return liferayFileItem.getContentType();
		}

		return null;
	}

	@Override
	public File getFile(String name) {
		return getFile(name, false);
	}

	@Override
	public File getFile(String name, boolean forceCreate) {
		if (getFileName(name) == null) {
			return null;
		}

		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isEmpty(liferayFileItems)) {
			return null;
		}

		FileItem liferayFileItem = liferayFileItems[0];

		if (!liferayFileItem.isInMemory()) {
			return liferayFileItem.getStoreLocation();
		}

		long size = liferayFileItem.getSize();

		if ((size > 0) && (size <= liferayFileItem.getSizeThreshold())) {
			forceCreate = true;
		}

		File file = liferayFileItem.getTempFile();

		if (forceCreate) {
			try {
				FileUtil.write(file, liferayFileItem.getInputStream());
			}
			catch (IOException ioException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to write temporary file " +
							file.getAbsolutePath(),
						ioException);
				}
			}
		}

		return file;
	}

	@Override
	public InputStream getFileAsStream(String name) throws IOException {
		return getFileAsStream(name, true);
	}

	@Override
	public InputStream getFileAsStream(String name, boolean deleteOnClose)
		throws IOException {

		if (getFileName(name) == null) {
			return null;
		}

		InputStream inputStream = null;

		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			FileItem liferayFileItem = liferayFileItems[0];

			inputStream = getInputStream(liferayFileItem, deleteOnClose);
		}

		return inputStream;
	}

	@Override
	public String getFileName(String name) {
		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			FileItem liferayFileItem = liferayFileItems[0];

			return liferayFileItem.getFileName();
		}

		return null;
	}

	@Override
	public String[] getFileNames(String name) {
		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			String[] fileNames = new String[liferayFileItems.length];

			for (int i = 0; i < liferayFileItems.length; i++) {
				FileItem liferayFileItem = liferayFileItems[i];

				fileNames[i] = liferayFileItem.getFileName();
			}

			return fileNames;
		}

		return null;
	}

	@Override
	public File[] getFiles(String name) {
		String[] fileNames = getFileNames(name);

		if (fileNames == null) {
			return null;
		}

		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			File[] files = new File[liferayFileItems.length];

			for (int i = 0; i < liferayFileItems.length; i++) {
				FileItem liferayFileItem = liferayFileItems[i];

				if (Validator.isNotNull(liferayFileItem.getFileName())) {
					if (liferayFileItem.isInMemory()) {
						files[i] = liferayFileItem.getTempFile();
					}
					else {
						files[i] = liferayFileItem.getStoreLocation();
					}
				}
			}

			return files;
		}

		return null;
	}

	@Override
	public InputStream[] getFilesAsStream(String name) throws IOException {
		return getFilesAsStream(name, true);
	}

	@Override
	public InputStream[] getFilesAsStream(String name, boolean deleteOnClose)
		throws IOException {

		String[] fileNames = getFileNames(name);

		if (fileNames == null) {
			return null;
		}

		InputStream[] inputStreams = null;

		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			inputStreams = new InputStream[liferayFileItems.length];

			for (int i = 0; i < liferayFileItems.length; i++) {
				FileItem liferayFileItem = liferayFileItems[i];

				if (Validator.isNotNull(liferayFileItem.getFileName())) {
					inputStreams[i] = getInputStream(
						liferayFileItem, deleteOnClose);
				}
			}
		}

		return inputStreams;
	}

	@Override
	public String getFullFileName(String name) {
		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			FileItem liferayFileItem = liferayFileItems[0];

			return liferayFileItem.getFullFileName();
		}

		return null;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (_liferayServletRequest != null) {
			return _liferayServletRequest.getInputStream();
		}

		return super.getInputStream();
	}

	@Override
	public Map<String, FileItem[]> getMultipartParameterMap() {
		return _fileParameters;
	}

	@Override
	public String getParameter(String name) {
		List<String> values = _regularParameters.get(name);

		if ((values != null) && !values.isEmpty()) {
			return values.get(0);
		}

		return super.getParameter(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> map = new HashMap<>();

		Enumeration<String> enumeration = getParameterNames();

		while (enumeration.hasMoreElements()) {
			String name = enumeration.nextElement();

			String[] values = getParameterValues(name);

			if (values != null) {
				map.put(name, values);
			}
		}

		return map;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		Set<String> parameterNames = new LinkedHashSet<>();

		Enumeration<String> enumeration = super.getParameterNames();

		while (enumeration.hasMoreElements()) {
			parameterNames.add(enumeration.nextElement());
		}

		parameterNames.addAll(_regularParameters.keySet());
		parameterNames.addAll(_fileParameters.keySet());

		return Collections.enumeration(parameterNames);
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] parameterValues = null;

		List<String> values = _regularParameters.get(name);

		if (values != null) {
			parameterValues = values.toArray(new String[0]);
		}

		String[] parentParameterValues = super.getParameterValues(name);

		if (parameterValues == null) {
			return parentParameterValues;
		}
		else if (parentParameterValues == null) {
			return parameterValues;
		}

		return ArrayUtil.append(parameterValues, parentParameterValues);
	}

	@Override
	public Map<String, List<String>> getRegularParameterMap() {
		return _regularParameters;
	}

	@Override
	public Long getSize(String name) {
		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			FileItem liferayFileItem = liferayFileItems[0];

			return Long.valueOf(liferayFileItem.getSize());
		}

		return null;
	}

	@Override
	public Boolean isFormField(String name) {
		FileItem[] liferayFileItems = _fileParameters.get(name);

		if (ArrayUtil.isNotEmpty(liferayFileItems)) {
			FileItem liferayFileItem = liferayFileItems[0];

			return liferayFileItem.isFormField();
		}

		return null;
	}

	protected InputStream getInputStream(
			FileItem liferayFileItem, boolean deleteOnClose)
		throws IOException {

		InputStream inputStream = null;

		if (liferayFileItem.isInMemory() && (liferayFileItem.getSize() > 0)) {
			inputStream = liferayFileItem.getInputStream();
		}
		else if (!liferayFileItem.isInMemory()) {
			inputStream = new ByteArrayFileInputStream(
				liferayFileItem.getStoreLocation(),
				liferayFileItem.getSizeThreshold(), deleteOnClose);
		}

		return inputStream;
	}

	private List<FileItem> _sort(List<FileItem> fileItems) {
		Map<String, GroupedFileItems> groupedFileItemsMap = new HashMap<>();

		for (FileItem fileItem : fileItems) {
			String fieldName = fileItem.getFieldName();

			GroupedFileItems groupedFileItems = groupedFileItemsMap.get(
				fieldName);

			if (groupedFileItems == null) {
				groupedFileItems = new GroupedFileItems(fieldName);

				groupedFileItemsMap.put(fieldName, groupedFileItems);
			}

			groupedFileItems.addFileItem(fileItem);
		}

		Set<GroupedFileItems> groupedFileItemsList = new TreeSet<>(
			groupedFileItemsMap.values());

		List<FileItem> sortedFileItems = new ArrayList<>();

		for (GroupedFileItems groupedFileItems : groupedFileItemsList) {
			sortedFileItems.addAll(groupedFileItems.getFileItems());
		}

		return sortedFileItems;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UploadServletRequestImpl.class);

	private static final Snapshot<ServletFileUpload>
		_servletFileUploadSnapshot = new Snapshot<>(
			UploadServletRequestImpl.class, ServletFileUpload.class);

	private final Map<String, FileItem[]> _fileParameters;
	private final LiferayServletRequest _liferayServletRequest;
	private final Map<String, List<String>> _regularParameters;

	private static class GroupedFileItems
		implements Comparable<GroupedFileItems> {

		public GroupedFileItems(String key) {
			_key = key;
		}

		public void addFileItem(FileItem fileItem) {
			_fileItems.add(fileItem);

			_fileItemsSize += fileItem.getSize();
		}

		@Override
		public int compareTo(GroupedFileItems groupedFileItems) {
			if (groupedFileItems == null) {
				return 1;
			}

			if (equals(groupedFileItems)) {
				return 0;
			}

			if (_key.equals(groupedFileItems._key) ||
				(getFileItemsSize() >= groupedFileItems.getFileItemsSize())) {

				return 1;
			}

			return -1;
		}

		public List<FileItem> getFileItems() {
			return _fileItems;
		}

		public int getFileItemsSize() {
			return _fileItemsSize;
		}

		private final List<FileItem> _fileItems = new ArrayList<>();
		private int _fileItemsSize;
		private final String _key;

	}

}