/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tika.internal.metadata;

import com.drew.imaging.png.PngMetadataReader;
import com.drew.metadata.png.PngDirectory;

import com.liferay.dynamic.data.mapping.kernel.DDMForm;
import com.liferay.dynamic.data.mapping.kernel.DDMFormField;
import com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.kernel.UnlocalizedValue;
import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.metadata.RawMetadataProcessor;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.tika.internal.configuration.helper.TikaConfigurationHelper;
import com.liferay.portal.tika.internal.util.ProcessConfigUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TIFF;
import org.apache.tika.metadata.XMPDM;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Miguel Pastor
 * @author Alexander Chow
 * @author Shuyang Zhou
 */
@Component(service = RawMetadataProcessor.class)
public class TikaRawMetadataProcessor implements RawMetadataProcessor {

	@Override
	public Set<String> getFieldNames() {
		return _fields.keySet();
	}

	@Override
	public Map<String, DDMFormValues> getRawMetadataMap(
			String mimeType, InputStream inputStream)
		throws PortalException {

		Metadata metadata = _extractMetadata(mimeType, inputStream);

		return _createDDMFormValuesMap(metadata);
	}

	private DDMForm _createDDMForm(Locale defaultLocale) {
		DDMForm ddmForm = new DDMForm();

		ddmForm.addAvailableLocale(defaultLocale);
		ddmForm.setDefaultLocale(defaultLocale);

		return ddmForm;
	}

	private DDMFormValues _createDDMFormValues(Metadata metadata) {
		Locale defaultLocale = LocaleUtil.getDefault();

		DDMForm ddmForm = _createDDMForm(defaultLocale);

		DDMFormValues ddmFormValues = new DDMFormValues(ddmForm);

		ddmFormValues.addAvailableLocale(defaultLocale);
		ddmFormValues.setDefaultLocale(defaultLocale);

		for (Map.Entry<String, String> entry : _fields.entrySet()) {
			String value = metadata.get(entry.getValue());

			if (value == null) {
				continue;
			}

			String name = entry.getKey();

			DDMFormField ddmFormField = _createTextDDMFormField(name);

			ddmForm.addDDMFormField(ddmFormField);

			DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

			ddmFormFieldValue.setName(name);
			ddmFormFieldValue.setValue(new UnlocalizedValue(value));

			ddmFormValues.addDDMFormFieldValue(ddmFormFieldValue);
		}

		return ddmFormValues;
	}

	private Map<String, DDMFormValues> _createDDMFormValuesMap(
		Metadata metadata) {

		Map<String, DDMFormValues> ddmFormValuesMap = new HashMap<>();

		if (metadata == null) {
			return ddmFormValuesMap;
		}

		DDMFormValues ddmFormValues = _createDDMFormValues(metadata);

		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
			ddmFormValues.getDDMFormFieldValuesMap();

		Set<String> names = ddmFormFieldValuesMap.keySet();

		if (!names.isEmpty()) {
			ddmFormValuesMap.put(TIKA_RAW_METADATA, ddmFormValues);
		}

		return ddmFormValuesMap;
	}

	private DDMFormField _createTextDDMFormField(String name) {
		DDMFormField ddmFormField = new DDMFormField(name, "text");

		ddmFormField.setDataType("string");

		return ddmFormField;
	}

	private Metadata _extractMetadata(
		String mimeType, InputStream inputStream) {

		if (Objects.equals(mimeType, ContentTypes.IMAGE_PNG)) {
			return _getPNGMetadata(inputStream);
		}

		return _getMetadata(mimeType, inputStream);
	}

	private Metadata _getMetadata(String mimeType, InputStream inputStream) {
		Parser parser = new AutoDetectParser(
			_tikaConfigurationHelper.getTikaConfig());

		if (_tikaConfigurationHelper.useForkProcess(mimeType)) {
			File file = FileUtil.createTempFile();

			try {
				FileUtil.write(file, inputStream);

				if (file.length() == 0) {
					return null;
				}

				ExtractMetadataProcessCallable extractMetadataProcessCallable =
					new ExtractMetadataProcessCallable(file, parser);

				ProcessChannel<Metadata> processChannel =
					_processExecutor.execute(
						ProcessConfigUtil.getProcessConfig(),
						extractMetadataProcessCallable);

				Future<Metadata> future =
					processChannel.getProcessNoticeableFuture();

				return _postProcessMetadata(mimeType, future.get());
			}
			catch (Exception exception) {
				throw new SystemException(exception);
			}
			finally {
				file.delete();
			}
		}

		try {
			return _postProcessMetadata(
				mimeType,
				ExtractMetadataProcessCallable._extractMetadata(
					inputStream, parser));
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}
	}

	private Metadata _getPNGMetadata(InputStream inputStream) {
		Metadata metadata = new Metadata();

		try {
			com.drew.metadata.Metadata inputStreamMetadata =
				PngMetadataReader.readMetadata(inputStream);

			PngDirectory pngDirectory =
				inputStreamMetadata.getFirstDirectoryOfType(PngDirectory.class);

			if (pngDirectory == null) {
				return metadata;
			}

			if (pngDirectory.containsTag(PngDirectory.TAG_IMAGE_HEIGHT) &&
				pngDirectory.containsTag(PngDirectory.TAG_IMAGE_WIDTH)) {

				metadata.set(
					TIFF.IMAGE_LENGTH,
					pngDirectory.getInt(PngDirectory.TAG_IMAGE_HEIGHT));
				metadata.set(
					TIFF.IMAGE_WIDTH,
					pngDirectory.getInt(PngDirectory.TAG_IMAGE_WIDTH));
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return metadata;
	}

	private Metadata _postProcessMetadata(String mimeType, Metadata metadata) {
		if (mimeType.equals(ContentTypes.IMAGE_SVG_XML) && (metadata != null)) {
			String contentType = metadata.get(HttpHeaders.CONTENT_TYPE);

			if (contentType.startsWith(ContentTypes.TEXT_PLAIN)) {
				metadata.set(
					HttpHeaders.CONTENT_TYPE,
					StringUtil.replace(
						contentType, ContentTypes.TEXT_PLAIN,
						ContentTypes.IMAGE_SVG_XML));
			}
		}

		if (mimeType.endsWith(ContentTypes.APPLICATION_JAVASCRIPT)) {
			String contentType = metadata.get(HttpHeaders.CONTENT_TYPE);

			if (contentType.startsWith(ContentTypes.TEXT_XMATLAB)) {
				metadata.set(
					HttpHeaders.CONTENT_TYPE,
					StringUtil.replace(
						contentType, ContentTypes.TEXT_XMATLAB,
						ContentTypes.APPLICATION_JAVASCRIPT));
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Extracted metadata: " + metadata);
		}

		return metadata;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TikaRawMetadataProcessor.class);

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

	@Reference
	private ProcessExecutor _processExecutor;

	@Reference
	private TikaConfigurationHelper _tikaConfigurationHelper;

	private static class ExtractMetadataProcessCallable
		implements ProcessCallable<Metadata> {

		@Override
		public Metadata call() throws ProcessException {
			Logger logger = Logger.getLogger(
				"org.apache.tika.parser.SQLite3Parser");

			logger.setLevel(Level.SEVERE);

			logger = Logger.getLogger("org.apache.tika.parsers.PDFParser");

			logger.setLevel(Level.SEVERE);

			try (InputStream inputStream = new FileInputStream(_file)) {
				return _extractMetadata(inputStream, _parser);
			}
			catch (IOException ioException) {
				throw new ProcessException(ioException);
			}
		}

		private static Metadata _extractMetadata(
				InputStream inputStream, Parser parser)
			throws IOException {

			Metadata metadata = new Metadata();

			ParseContext parseContext = new ParseContext();

			parseContext.set(Parser.class, parser);

			try {
				parser.parse(
					inputStream, new DefaultHandler(), metadata, parseContext);
			}
			catch (SAXException | TikaException exception) {
				throw new IOException(exception);
			}

			// Remove potential security risks

			metadata.remove(XMPDM.ABS_PEAK_AUDIO_FILE_PATH.getName());
			metadata.remove(XMPDM.RELATIVE_PEAK_AUDIO_FILE_PATH.getName());

			return metadata;
		}

		private ExtractMetadataProcessCallable(File file, Parser parser) {
			_file = file;
			_parser = parser;
		}

		private static final long serialVersionUID = 1L;

		private final File _file;
		private final Parser _parser;

	}

}