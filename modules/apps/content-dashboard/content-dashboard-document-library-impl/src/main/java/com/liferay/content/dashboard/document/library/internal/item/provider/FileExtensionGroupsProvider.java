/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.provider;

import com.liferay.document.library.configuration.DLConfiguration;
import com.liferay.document.library.display.context.DLMimeTypeDisplayContext;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MimeTypes;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 * @author Cristina González
 */
@Component(
	configurationPid = "com.liferay.document.library.configuration.DLConfiguration",
	service = FileExtensionGroupsProvider.class
)
public class FileExtensionGroupsProvider {

	public List<FileExtensionGroup> getFileExtensionGroups() {
		return _fileExtensionGroups;
	}

	public String getFileGroupKey(String extension) {
		if (_fileExtensionGroups.isEmpty()) {
			return _OTHER;
		}

		for (FileExtensionGroup fileExtensionGroup : _fileExtensionGroups) {
			if (fileExtensionGroup.containsExtension(extension)) {
				return fileExtensionGroup.getKey();
			}
		}

		return _OTHER;
	}

	public boolean isOther(String extension) {
		return !_extensionMimeTypes.containsKey(extension);
	}

	public static class FileExtensionGroup
		implements Comparable<FileExtensionGroup> {

		public FileExtensionGroup(
			String[] extensions, String key, String[] mimeTypes) {

			_extensions = extensions;
			_key = key;
			_mimeTypes = mimeTypes;
		}

		@Override
		public int compareTo(FileExtensionGroup fileExtensionGroup) {
			if (Objects.equals(_OTHER, fileExtensionGroup._key)) {
				return -1;
			}
			else if (Objects.equals(_OTHER, _key)) {
				return 1;
			}

			return _key.compareTo(fileExtensionGroup.getKey());
		}

		public boolean containsExtension(String extension) {
			if (ArrayUtil.isNotEmpty(_extensions)) {
				return ArrayUtil.contains(_extensions, extension);
			}

			return false;
		}

		public String getKey() {
			return _key;
		}

		public JSONObject toJSONObject(
			Set<String> checkedFileExtensions,
			List<String> filterFileExtensions,
			DLMimeTypeDisplayContext dlMimeTypeDisplayContext,
			Language language, Locale locale, Set<String> otherFileExtensions) {

			List<String> extensions = ListUtil.filter(
				filterFileExtensions,
				filterFileExtension -> {
					if (!Objects.equals(_key, _OTHER)) {
						return ArrayUtil.contains(
							_extensions, filterFileExtension);
					}

					return otherFileExtensions.contains(filterFileExtension);
				});

			if (extensions.isEmpty()) {
				return null;
			}

			return JSONUtil.put(
				"fileExtensions",
				() -> JSONUtil.toJSONArray(
					extensions,
					extension -> JSONUtil.put(
						"fileExtension", extension
					).put(
						"selected", checkedFileExtensions.contains(extension)
					))
			).put(
				"icon", _getIcon(dlMimeTypeDisplayContext)
			).put(
				"iconCssClass", _getIconCssClass(dlMimeTypeDisplayContext)
			).put(
				"label", language.get(locale, getKey())
			);
		}

		private String _getIcon(
			DLMimeTypeDisplayContext dlMimeTypeDisplayContext) {

			if (ArrayUtil.isEmpty(_extensions)) {
				return "document-default";
			}

			return dlMimeTypeDisplayContext.getIconFileMimeType(_mimeTypes[0]);
		}

		private String _getIconCssClass(
			DLMimeTypeDisplayContext dlMimeTypeDisplayContext) {

			if (ArrayUtil.isEmpty(_extensions)) {
				return null;
			}

			return dlMimeTypeDisplayContext.getCssClassFileMimeType(
				_mimeTypes[0]);
		}

		private final String[] _extensions;
		private final String _key;
		private final String[] _mimeTypes;

	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) throws Exception {
		_extensionMimeTypes = new HashMap<>();

		_dlConfiguration = ConfigurableUtil.createConfigurable(
			DLConfiguration.class, properties);

		_fileExtensionGroups = TransformUtil.transform(
			_fileExtensionMimetype.entrySet(),
			entry -> _createFileExtensionGroup(
				entry.getKey(), entry.getValue()));

		_fileExtensionGroups.sort(Comparator.naturalOrder());
	}

	private FileExtensionGroup _createFileExtensionGroup(
		String key, Function<DLConfiguration, String[]> function) {

		String[] mimeTypes = function.apply(_dlConfiguration);

		List<String> extensions = new ArrayList<>();

		for (String mimeType : mimeTypes) {
			for (String extensionsMimeType :
					_mimeTypes.getExtensions(mimeType)) {

				String extension = extensionsMimeType.replaceAll(
					"^\\.", StringPool.BLANK);

				_extensionMimeTypes.put(extension, mimeType);

				extensions.add(extension);
			}
		}

		extensions.sort(Comparator.naturalOrder());

		return new FileExtensionGroup(
			extensions.toArray(new String[0]), key, mimeTypes);
	}

	private static final String _OTHER = "other";

	private static final Map<String, Function<DLConfiguration, String[]>>
		_fileExtensionMimetype =
			HashMapBuilder.<String, Function<DLConfiguration, String[]>>put(
				_OTHER, dlConfiguration -> new String[0]
			).put(
				"audio",
				dlConfiguration ->
					PropsValues.DL_FILE_ENTRY_PREVIEW_AUDIO_MIME_TYPES
			).put(
				"code", dlConfiguration -> dlConfiguration.codeFileMimeTypes()
			).put(
				"compressed",
				dlConfiguration -> dlConfiguration.compressedFileMimeTypes()
			).put(
				"image",
				dlConfiguration ->
					PropsValues.DL_FILE_ENTRY_PREVIEW_IMAGE_MIME_TYPES
			).put(
				"presentation",
				dlConfiguration -> dlConfiguration.presentationFileMimeTypes()
			).put(
				"spreadsheet",
				dlConfiguration -> dlConfiguration.spreadSheetFileMimeTypes()
			).put(
				"text", dlConfiguration -> dlConfiguration.textFileMimeTypes()
			).put(
				"vectorial",
				dlConfiguration -> dlConfiguration.vectorialFileMimeTypes()
			).put(
				"video",
				dlConfiguration ->
					PropsValues.DL_FILE_ENTRY_PREVIEW_VIDEO_MIME_TYPES
			).build();

	private volatile DLConfiguration _dlConfiguration;
	private volatile Map<String, String> _extensionMimeTypes;
	private volatile List<FileExtensionGroup> _fileExtensionGroups =
		new ArrayList<>();

	@Reference
	private MimeTypes _mimeTypes;

}