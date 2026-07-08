/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.util;

import com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.kernel.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.metadata.RawMetadataProcessor;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Shuyang Zhou
 */
public class DDMFormUtil {

	public static DDMForm buildDDMForm(Locale locale) {
		DDMForm ddmForm = new DDMForm();

		ddmForm.setAvailableLocales(Collections.singleton(locale));
		ddmForm.setDDMFormFields(
			TransformUtil.transform(
				_fields.keySet(),
				fieldName -> {
					DDMFormField ddmFormField = new DDMFormField(
						fieldName, "text");

					ddmFormField.setDataType("string");
					ddmFormField.setIndexType("text");
					ddmFormField.setLocalizable(false);
					ddmFormField.setMultiple(false);
					ddmFormField.setReadOnly(false);
					ddmFormField.setRepeatable(false);
					ddmFormField.setRequired(false);
					ddmFormField.setShowLabel(true);

					LocalizedValue label = ddmFormField.getLabel();

					label.addString(
						locale,
						"metadata.".concat(
							StringUtil.replaceFirst(
								fieldName, CharPool.UNDERLINE,
								CharPool.PERIOD)));
					label.setDefaultLocale(locale);

					LocalizedValue predefinedValue =
						ddmFormField.getPredefinedValue();

					predefinedValue.addString(locale, StringPool.BLANK);
					predefinedValue.setDefaultLocale(locale);

					LocalizedValue style = ddmFormField.getStyle();

					style.setDefaultLocale(locale);

					LocalizedValue tip = ddmFormField.getTip();

					tip.setDefaultLocale(locale);

					DDMFormFieldOptions ddmFormFieldOptions =
						ddmFormField.getDDMFormFieldOptions();

					ddmFormFieldOptions.setDefaultLocale(locale);

					return ddmFormField;
				}));
		ddmForm.setDefaultLocale(locale);

		return ddmForm;
	}

	public static Map<String, DDMFormValues> getDDMFormValuesMap(
		Map<String, String[]> rawMetadata) {

		Locale defaultLocale = LocaleUtil.getDefault();

		com.liferay.dynamic.data.mapping.kernel.DDMForm ddmForm =
			new com.liferay.dynamic.data.mapping.kernel.DDMForm();

		ddmForm.addAvailableLocale(defaultLocale);
		ddmForm.setDefaultLocale(defaultLocale);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.addAvailableLocale(defaultLocale);
		ddmFormValues.setDefaultLocale(defaultLocale);

		for (Map.Entry<String, String> entry : _fields.entrySet()) {
			String[] values = rawMetadata.get(entry.getValue());

			if (ArrayUtil.isEmpty(values) || (values[0] == null)) {
				continue;
			}

			String name = entry.getKey();

			com.liferay.dynamic.data.mapping.kernel.DDMFormField ddmFormField =
				new com.liferay.dynamic.data.mapping.kernel.DDMFormField(
					name, "text");

			ddmFormField.setDataType("string");

			ddmForm.addDDMFormField(ddmFormField);

			DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

			ddmFormFieldValue.setName(name);
			ddmFormFieldValue.setValue(new UnlocalizedValue(values[0]));

			ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
		}

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap();

		if (ddmFormFieldValuesMap.isEmpty()) {
			return new HashMap<>();
		}

		return HashMapBuilder.<String, DDMFormValues>put(
			RawMetadataProcessor.TIKA_RAW_METADATA, ddmFormValues
		).build();
	}

	private static final Map<String, String> _fields = HashMapBuilder.put(
		"ClimateForcast_ACKNOWLEDGEMENT", "acknowledgement"
	).put(
		"ClimateForcast_COMMAND_LINE", "cmd_ln"
	).put(
		"ClimateForcast_COMMENT", "comment"
	).put(
		"ClimateForcast_CONTACT", "contact"
	).put(
		"ClimateForcast_CONVENTIONS", "Conventions"
	).put(
		"ClimateForcast_EXPERIMENT_ID", "experiment_id"
	).put(
		"ClimateForcast_HISTORY", "history"
	).put(
		"ClimateForcast_INSTITUTION", "institution"
	).put(
		"ClimateForcast_MODEL_NAME_ENGLISH", "model_name_english"
	).put(
		"ClimateForcast_PROGRAM_ID", "prg_ID"
	).put(
		"ClimateForcast_PROJECT_ID", "project_id"
	).put(
		"ClimateForcast_REALIZATION", "realization"
	).put(
		"ClimateForcast_REFERENCES", "references"
	).put(
		"ClimateForcast_SOURCE", "source"
	).put(
		"ClimateForcast_TABLE_ID", "table_id"
	).put(
		"CreativeCommons_LICENSE_LOCATION", "License-Location"
	).put(
		"CreativeCommons_LICENSE_URL", "License-Url"
	).put(
		"CreativeCommons_WORK_TYPE", "Work-Type"
	).put(
		"DublinCore_CONTRIBUTOR", "dc:contributor"
	).put(
		"DublinCore_COVERAGE", "dc:coverage"
	).put(
		"DublinCore_CREATED", "dcterms:created"
	).put(
		"DublinCore_CREATOR", "dc:creator"
	).put(
		"DublinCore_DATE", "dc:date"
	).put(
		"DublinCore_DESCRIPTION", "dc:description"
	).put(
		"DublinCore_FORMAT", "dc:format"
	).put(
		"DublinCore_IDENTIFIER", "dc:identifier"
	).put(
		"DublinCore_LANGUAGE", "dc:language"
	).put(
		"DublinCore_MODIFIED", "dcterms:modified"
	).put(
		"DublinCore_NAMESPACE_URI_DC", "http://purl.org/dc/elements/1.1/"
	).put(
		"DublinCore_NAMESPACE_URI_DC_TERMS", "http://purl.org/dc/terms/"
	).put(
		"DublinCore_PREFIX_DC", "dc"
	).put(
		"DublinCore_PREFIX_DC_TERMS", "dcterms"
	).put(
		"DublinCore_PUBLISHER", "dc:publisher"
	).put(
		"DublinCore_RELATION", "dc:relation"
	).put(
		"DublinCore_RIGHTS", "dc:rights"
	).put(
		"DublinCore_SOURCE", "dc:source"
	).put(
		"DublinCore_SUBJECT", "dc:subject"
	).put(
		"DublinCore_TITLE", "dc:title"
	).put(
		"DublinCore_TYPE", "dc:type"
	).put(
		"Geographic_ALTITUDE", "geo:alt"
	).put(
		"Geographic_LATITUDE", "geo:lat"
	).put(
		"Geographic_LONGITUDE", "geo:long"
	).put(
		"HttpHeaders_CONTENT_DISPOSITION", "Content-Disposition"
	).put(
		"HttpHeaders_CONTENT_ENCODING", "Content-Encoding"
	).put(
		"HttpHeaders_CONTENT_LANGUAGE", "Content-Language"
	).put(
		"HttpHeaders_CONTENT_LENGTH", "Content-Length"
	).put(
		"HttpHeaders_CONTENT_LOCATION", "Content-Location"
	).put(
		"HttpHeaders_CONTENT_MD5", "Content-MD5"
	).put(
		"HttpHeaders_CONTENT_TYPE", "Content-Type"
	).put(
		"HttpHeaders_LAST_MODIFIED", "Last-Modified"
	).put(
		"HttpHeaders_LOCATION", "Location"
	).put(
		"Message_MESSAGE_BCC", "Message-Bcc"
	).put(
		"Message_MESSAGE_BCC_DISPLAY_NAME", "Message:BCC-Display-Name"
	).put(
		"Message_MESSAGE_BCC_EMAIL", "Message:BCC-Email"
	).put(
		"Message_MESSAGE_BCC_NAME", "Message:BCC-Name"
	).put(
		"Message_MESSAGE_CC", "Message-Cc"
	).put(
		"Message_MESSAGE_CC_DISPLAY_NAME", "Message:CC-Display-Name"
	).put(
		"Message_MESSAGE_CC_EMAIL", "Message:CC-Email"
	).put(
		"Message_MESSAGE_CC_NAME", "Message:CC-Name"
	).put(
		"Message_MESSAGE_FROM", "Message-From"
	).put(
		"Message_MESSAGE_FROM_EMAIL", "Message:From-Email"
	).put(
		"Message_MESSAGE_FROM_NAME", "Message:From-Name"
	).put(
		"Message_MESSAGE_PREFIX", "Message:"
	).put(
		"Message_MESSAGE_RAW_HEADER_PREFIX", "Message:Raw-Header:"
	).put(
		"Message_MESSAGE_RECIPIENT_ADDRESS", "Message-Recipient-Address"
	).put(
		"Message_MESSAGE_TO", "Message-To"
	).put(
		"Message_MESSAGE_TO_DISPLAY_NAME", "Message:To-Display-Name"
	).put(
		"Message_MESSAGE_TO_EMAIL", "Message:To-Email"
	).put(
		"Message_MESSAGE_TO_NAME", "Message:To-Name"
	).put(
		"Message_MULTIPART_BOUNDARY", "Multipart-Boundary"
	).put(
		"Message_MULTIPART_SUBTYPE", "Multipart-Subtype"
	).put(
		"Office_AUTHOR", "meta:author"
	).put(
		"Office_CHARACTER_COUNT", "meta:character-count"
	).put(
		"Office_CHARACTER_COUNT_WITH_SPACES", "meta:character-count-with-spaces"
	).put(
		"Office_CREATION_DATE", "meta:creation-date"
	).put(
		"Office_IMAGE_COUNT", "meta:image-count"
	).put(
		"Office_INITIAL_AUTHOR", "meta:initial-author"
	).put(
		"Office_KEYWORDS", "meta:keyword"
	).put(
		"Office_LAST_AUTHOR", "meta:last-author"
	).put(
		"Office_LINE_COUNT", "meta:line-count"
	).put(
		"Office_MAPI_FROM_REPRESENTING_EMAIL",
		"meta:mapi-from-representing-email"
	).put(
		"Office_MAPI_FROM_REPRESENTING_NAME", "meta:mapi-from-representing-name"
	).put(
		"Office_MAPI_MESSAGE_CLASS", "meta:mapi-message-class"
	).put(
		"Office_MAPI_MESSAGE_CLIENT_SUBMIT_TIME",
		"meta:mapi-msg-client-submit-time"
	).put(
		"Office_MAPI_SENT_BY_SERVER_TYPE", "meta:mapi-sent-by-server-type"
	).put(
		"Office_NAMESPACE_URI_DOC_META",
		"urn:oasis:names:tc:opendocument:xmlns:meta:1.0"
	).put(
		"Office_OBJECT_COUNT", "meta:object-count"
	).put(
		"Office_PAGE_COUNT", "meta:page-count"
	).put(
		"Office_PARAGRAPH_COUNT", "meta:paragraph-count"
	).put(
		"Office_PREFIX_DOC_META", "meta"
	).put(
		"Office_PRINT_DATE", "meta:print-date"
	).put(
		"Office_SAVE_DATE", "meta:save-date"
	).put(
		"Office_SLIDE_COUNT", "meta:slide-count"
	).put(
		"Office_TABLE_COUNT", "meta:table-count"
	).put(
		"Office_USER_DEFINED_METADATA_NAME_PREFIX", "custom:"
	).put(
		"Office_WORD_COUNT", "meta:word-count"
	).put(
		"OfficeOpenXMLCore_CATEGORY", "cp:category"
	).put(
		"OfficeOpenXMLCore_CONTENT_STATUS", "cp:contentStatus"
	).put(
		"OfficeOpenXMLCore_LAST_MODIFIED_BY", "cp:lastModifiedBy"
	).put(
		"OfficeOpenXMLCore_LAST_PRINTED", "cp:lastPrinted"
	).put(
		"OfficeOpenXMLCore_NAMESPACE_URI",
		"http://schemas.openxmlformats.org/package/2006/metadata" +
			"/core-properties/"
	).put(
		"OfficeOpenXMLCore_PREFIX", "cp"
	).put(
		"OfficeOpenXMLCore_REVISION", "cp:revision"
	).put(
		"OfficeOpenXMLCore_SUBJECT", "cp:subject"
	).put(
		"OfficeOpenXMLCore_VERSION", "cp:version"
	).put(
		"TIFF_BITS_PER_SAMPLE", "tiff:BitsPerSample"
	).put(
		"TIFF_EQUIPMENT_MAKE", "tiff:Make"
	).put(
		"TIFF_EQUIPMENT_MODEL", "tiff:Model"
	).put(
		"TIFF_EXIF_PAGE_COUNT", "exif:PageCount"
	).put(
		"TIFF_EXPOSURE_TIME", "exif:ExposureTime"
	).put(
		"TIFF_F_NUMBER", "exif:FNumber"
	).put(
		"TIFF_FLASH_FIRED", "exif:Flash"
	).put(
		"TIFF_FOCAL_LENGTH", "exif:FocalLength"
	).put(
		"TIFF_IMAGE_LENGTH", "tiff:ImageLength"
	).put(
		"TIFF_IMAGE_WIDTH", "tiff:ImageWidth"
	).put(
		"TIFF_ISO_SPEED_RATINGS", "exif:IsoSpeedRatings"
	).put(
		"TIFF_ORIENTATION", "tiff:Orientation"
	).put(
		"TIFF_ORIGINAL_DATE", "exif:DateTimeOriginal"
	).put(
		"TIFF_RESOLUTION_HORIZONTAL", "tiff:XResolution"
	).put(
		"TIFF_RESOLUTION_UNIT", "tiff:ResolutionUnit"
	).put(
		"TIFF_RESOLUTION_VERTICAL", "tiff:YResolution"
	).put(
		"TIFF_SAMPLES_PER_PIXEL", "tiff:SamplesPerPixel"
	).put(
		"TIFF_SOFTWARE", "tiff:Software"
	).put(
		"TikaMetadataKeys_EMBEDDED_RELATIONSHIP_ID", "embeddedRelationshipId"
	).put(
		"TikaMetadataKeys_EMBEDDED_RESOURCE_TYPE", "embeddedResourceType"
	).put(
		"TikaMetadataKeys_EMBEDDED_STORAGE_CLASS_ID", "embeddedStorageClassId"
	).put(
		"TikaMetadataKeys_PROTECTED", "protected"
	).put(
		"TikaMetadataKeys_RESOURCE_NAME_KEY", "resourceName"
	).put(
		"TikaMimeKeys_MIME_TYPE_MAGIC", "mime.type.magic"
	).put(
		"TikaMimeKeys_TIKA_MIME_FILE", "tika.mime.file"
	).put(
		"XMPDM_ABS_PEAK_AUDIO_FILE_PATH", "xmpDM:absPeakAudioFilePath"
	).put(
		"XMPDM_ALBUM", "xmpDM:album"
	).put(
		"XMPDM_ALBUM_ARTIST", "xmpDM:albumArtist"
	).put(
		"XMPDM_ALT_TAPE_NAME", "xmpDM:altTapeName"
	).put(
		"XMPDM_ARTIST", "xmpDM:artist"
	).put(
		"XMPDM_AUDIO_CHANNEL_TYPE", "xmpDM:audioChannelType"
	).put(
		"XMPDM_AUDIO_COMPRESSOR", "xmpDM:audioCompressor"
	).put(
		"XMPDM_AUDIO_MOD_DATE", "xmpDM:audioModDate"
	).put(
		"XMPDM_AUDIO_SAMPLE_RATE", "xmpDM:audioSampleRate"
	).put(
		"XMPDM_AUDIO_SAMPLE_TYPE", "xmpDM:audioSampleType"
	).put(
		"XMPDM_COMPILATION", "xmpDM:compilation"
	).put(
		"XMPDM_COMPOSER", "xmpDM:composer"
	).put(
		"XMPDM_COPYRIGHT", "xmpDM:copyright"
	).put(
		"XMPDM_DISC_NUMBER", "xmpDM:discNumber"
	).put(
		"XMPDM_DURATION", "xmpDM:duration"
	).put(
		"XMPDM_ENGINEER", "xmpDM:engineer"
	).put(
		"XMPDM_FILE_DATA_RATE", "xmpDM:fileDataRate"
	).put(
		"XMPDM_GENRE", "xmpDM:genre"
	).put(
		"XMPDM_INSTRUMENT", "xmpDM:instrument"
	).put(
		"XMPDM_KEY", "xmpDM:key"
	).put(
		"XMPDM_LOG_COMMENT", "xmpDM:logComment"
	).put(
		"XMPDM_LOOP", "xmpDM:loop"
	).put(
		"XMPDM_METADATA_MOD_DATE", "xmpDM:metadataModDate"
	).put(
		"XMPDM_NUMBER_OF_BEATS", "xmpDM:numberOfBeats"
	).put(
		"XMPDM_PULL_DOWN", "xmpDM:pullDown"
	).put(
		"XMPDM_RELATIVE_PEAK_AUDIO_FILE_PATH", "xmpDM:relativePeakAudioFilePath"
	).put(
		"XMPDM_RELEASE_DATE", "xmpDM:releaseDate"
	).put(
		"XMPDM_SCALE_TYPE", "xmpDM:scaleType"
	).put(
		"XMPDM_SCENE", "xmpDM:scene"
	).put(
		"XMPDM_SHOT_DATE", "xmpDM:shotDate"
	).put(
		"XMPDM_SHOT_LOCATION", "xmpDM:shotLocation"
	).put(
		"XMPDM_SHOT_NAME", "xmpDM:shotName"
	).put(
		"XMPDM_SPEAKER_PLACEMENT", "xmpDM:speakerPlacement"
	).put(
		"XMPDM_STRETCH_MODE", "xmpDM:stretchMode"
	).put(
		"XMPDM_TAPE_NAME", "xmpDM:tapeName"
	).put(
		"XMPDM_TEMPO", "xmpDM:tempo"
	).put(
		"XMPDM_TIME_SIGNATURE", "xmpDM:timeSignature"
	).put(
		"XMPDM_TRACK_NUMBER", "xmpDM:trackNumber"
	).put(
		"XMPDM_VIDEO_ALPHA_MODE", "xmpDM:videoAlphaMode"
	).put(
		"XMPDM_VIDEO_ALPHA_UNITY_IS_TRANSPARENT",
		"xmpDM:videoAlphaUnityIsTransparent"
	).put(
		"XMPDM_VIDEO_COLOR_SPACE", "xmpDM:videoColorSpace"
	).put(
		"XMPDM_VIDEO_COMPRESSOR", "xmpDM:videoCompressor"
	).put(
		"XMPDM_VIDEO_FIELD_ORDER", "xmpDM:videoFieldOrder"
	).put(
		"XMPDM_VIDEO_FRAME_RATE", "xmpDM:videoFrameRate"
	).put(
		"XMPDM_VIDEO_MOD_DATE", "xmpDM:videoModDate"
	).put(
		"XMPDM_VIDEO_PIXEL_ASPECT_RATIO", "xmpDM:videoPixelAspectRatio"
	).put(
		"XMPDM_VIDEO_PIXEL_DEPTH", "xmpDM:videoPixelDepth"
	).build();

}