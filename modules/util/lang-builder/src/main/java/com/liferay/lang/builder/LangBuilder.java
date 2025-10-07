/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.lang.builder;

import com.liferay.lang.builder.comparator.LangBuilderCategoryComparator;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.io.unsync.UnsyncBufferedReader;
import com.liferay.portal.kernel.io.unsync.UnsyncStringReader;
import com.liferay.portal.kernel.language.LanguageBuilderUtil;
import com.liferay.portal.kernel.language.LanguageValidator;
import com.liferay.portal.kernel.language.constants.LanguageConstants;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.NaturalOrderStringComparator;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.tools.ArgumentsUtil;
import com.liferay.portal.tools.GitException;
import com.liferay.portal.tools.GitUtil;

import io.github.firemaples.language.Language;
import io.github.firemaples.translate.Translate;

import java.io.File;
import java.io.FileInputStream;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

/**
 * @author Brian Wing Shun Chan
 * @author Hugo Huijser
 */
public class LangBuilder {

	public static void main(String[] args) throws Exception {
		Map<String, String> arguments = new HashMap<>(
			ArgumentsUtil.parseArguments(args));

		System.setProperty("line.separator", StringPool.NEW_LINE);

		String excludedLanguageIdsString = GetterUtil.getString(
			arguments.get("lang.excluded.language.ids"),
			StringUtil.merge(LangBuilderArgs.EXCLUDED_LANGUAGE_IDS));
		String langDirName = GetterUtil.getString(
			arguments.get(LanguageConstants.KEY_DIR),
			LangBuilderArgs.LANG_DIR_NAME);
		String langFileName = GetterUtil.getString(
			arguments.get("lang.file"), LangBuilderArgs.LANG_FILE_NAME);
		boolean titleCapitalization = GetterUtil.getBoolean(
			arguments.get("lang.title.capitalization"),
			LangBuilderArgs.TITLE_CAPITALIZATION);
		boolean translate = GetterUtil.getBoolean(
			arguments.get("lang.translate"), LangBuilderArgs.TRANSLATE);
		String translateSubscriptionKey = arguments.get(
			"lang.translate.subscription.key");

		boolean buildCurrentBranch = ArgumentsUtil.getBoolean(
			arguments, "build.current.branch", false);

		String[] excludedLanguageIds = StringUtil.split(
			excludedLanguageIdsString);

		if (buildCurrentBranch) {
			String gitWorkingBranchName = ArgumentsUtil.getString(
				arguments, "git.working.branch.name", "master");

			_processCurrentBranch(
				excludedLanguageIds, langFileName, titleCapitalization,
				translate, translateSubscriptionKey, gitWorkingBranchName);

			return;
		}

		try {
			new LangBuilder(
				excludedLanguageIds, langDirName, langFileName,
				titleCapitalization, translate, translateSubscriptionKey);
		}
		catch (Exception exception) {
			ArgumentsUtil.processMainException(arguments, exception);
		}
	}

	public LangBuilder(
			String[] excludedLanguageIds, String langDirName,
			String langFileName, boolean titleCapitalization, boolean translate,
			String translateSubscriptionKey)
		throws Exception {

		_excludedLanguageIds = excludedLanguageIds;
		_langDirName = langDirName;
		_langFileName = langFileName;
		_titleCapitalization = titleCapitalization;

		if (Validator.isNull(translateSubscriptionKey)) {
			System.out.println(
				"Translation is disabled because credentials are not " +
					"specified");

			_translate = false;
		}
		else {
			_translate = translate;
		}

		Translate.setSubscriptionKey(translateSubscriptionKey);

		_initKeysWithUpdatedValues();

		File renameKeysFile = new File(_langDirName + "/rename.properties");

		if (renameKeysFile.exists()) {
			_renameKeys = _readProperties(renameKeysFile);
		}
		else {
			_renameKeys = null;
		}

		File propertiesFile = new File(
			StringBundler.concat(
				langDirName, "/", langFileName, ".properties"));

		if (!propertiesFile.exists()) {
			return;
		}

		String content = _orderProperties(propertiesFile, false);

		_copyProperties(propertiesFile, "en");

		// Automatic copy locales

		_createProperties(content, "ar"); // Arabic
		_createProperties(content, "eu"); // Basque
		_createProperties(content, "bg"); // Bulgarian
		_createProperties(content, "my"); // Burmese (Myanmar)
		_createProperties(content, "km"); // Cambodian
		_createProperties(content, "ca"); // Catalan
		_createProperties(content, "zh_CN"); // Chinese (China)
		_createProperties(content, "zh_TW"); // Chinese (Taiwan)
		_createProperties(content, "hr"); // Croatian
		_createProperties(content, "hr_BA"); // Croatian (Bosnia and Herzegovina)
		_createProperties(content, "cs"); // Czech
		_createProperties(content, "da"); // Danish
		_createProperties(content, "nl"); // Dutch (Netherlands)
		_createProperties(content, "nl_BE", "nl"); // Dutch (Belgium)
		_createProperties(content, "en_AU"); // English (Australia)
		_createProperties(content, "en_CA"); // English (Canada)
		_createProperties(content, "en_IE"); // English (Ireland)
		_createProperties(content, "en_GB"); // English (United Kingdom)
		_createProperties(content, "et"); // Estonian
		_createProperties(content, "fi"); // Finnish
		_createProperties(content, "fr"); // French
		_createProperties(content, "fr_BE", "fr"); // French (Belgium)
		_createProperties(content, "fr_CA"); // French (Canada)
		_createProperties(content, "gl"); // Galician
		_createProperties(content, "de"); // German
		_createProperties(content, "de_AT", "de"); // German (Austria)
		_createProperties(content, "el"); // Greek
		_createProperties(content, "iw"); // Hebrew
		_createProperties(content, "hi_IN"); // Hindi (India)
		_createProperties(content, "hu"); // Hungarian
		_createProperties(content, "in"); // Indonesian
		_createProperties(content, "it"); // Italian
		_createProperties(content, "it_CH", "it"); // Italian (Switzerland)
		_createProperties(content, "ja"); // Japanese
		_createProperties(content, "kk"); // Kazakh
		_createProperties(content, "ko"); // Korean
		_createProperties(content, "lo"); // Lao
		_createProperties(content, "lt"); // Lithuanian
		_createProperties(content, "mk"); // Macedonian
		_createProperties(content, "ms"); // Malay
		_createProperties(content, "no", "nb"); // Norwegian
		_createProperties(content, "nb"); // Norwegian Bokmål
		_createProperties(content, "fa"); // Persian
		_createProperties(content, "pl"); // Polish
		_createProperties(content, "pt_BR"); // Portuguese (Brazil)
		_createProperties(content, "pt_PT", "pt_BR"); // Portuguese (Portugal)
		_createProperties(content, "ro"); // Romanian
		_createProperties(content, "ru"); // Russian
		_createProperties(content, "sr_RS"); // Serbian (Cyrillic)
		_createProperties(content, "sr_RS_latin"); // Serbian (Latin)
		_createProperties(content, "sk"); // Slovak
		_createProperties(content, "sl"); // Slovene
		_createProperties(content, "es"); // Spanish
		_createProperties(content, "es_AR", "es"); // Spanish (Argentina)
		_createProperties(content, "es_CO", "es"); // Spanish (Colombia)
		_createProperties(content, "es_MX", "es"); // Spanish (Mexico)
		_createProperties(content, "sv"); // Swedish
		_createProperties(content, "fr_CH", "fr"); // Swiss French
		_createProperties(content, "de_CH", "de"); // Swiss German
		_createProperties(content, "ta_IN"); // Tamil
		_createProperties(content, "th"); // Thai
		_createProperties(content, "tr"); // Turkish
		_createProperties(content, "uk"); // Ukrainian
		_createProperties(content, "vi"); // Vietnamese
	}

	private static void _processCurrentBranch(
			String[] excludedLanguageIds, String langFileName,
			boolean titleCapitalization, boolean translate,
			String translateSubscriptionKey, String gitWorkingBranchName)
		throws Exception {

		try {
			String basedir = ".././";

			List<String> fileNames = GitUtil.getCurrentBranchFileNames(
				basedir, gitWorkingBranchName);

			for (String fileName : fileNames) {
				int pos = fileName.indexOf(
					"content/" + langFileName + ".properties");

				if (pos == -1) {
					continue;
				}

				String langDirName = basedir + fileName.substring(0, pos + 7);

				new LangBuilder(
					excludedLanguageIds, langDirName, langFileName,
					titleCapitalization, translate, translateSubscriptionKey);
			}
		}
		catch (GitException gitException) {
			System.out.println(gitException.getMessage());
		}
	}

	private void _addMessage(
		Map<LangBuilderCategory, Map<String, String>> messages, String key,
		String value, boolean useSingleCategory) {

		LangBuilderCategory langBuilderCategory = _getLangBuilderCategory(
			key, useSingleCategory);

		Map<String, String> categoryMessages = messages.get(
			langBuilderCategory);

		if (categoryMessages == null) {
			categoryMessages = new TreeMap<>(
				new NaturalOrderStringComparator(true, true));
		}

		categoryMessages.put(key, value);

		messages.put(langBuilderCategory, categoryMessages);
	}

	private void _copyProperties(File file, String languageId)
		throws Exception {

		Path path = Paths.get(
			_langDirName,
			StringBundler.concat(
				_langFileName, "_", languageId, ".properties"));

		Files.copy(file.toPath(), path, StandardCopyOption.REPLACE_EXISTING);
	}

	private void _createProperties(String content, String languageId)
		throws Exception {

		_createProperties(content, languageId, null);
	}

	private void _createProperties(
			String content, String languageId, String parentLanguageId)
		throws Exception {

		File propertiesFile = new File(
			StringBundler.concat(
				_langDirName, "/", _langFileName, "_", languageId,
				".properties"));

		Properties properties = new Properties();

		if (propertiesFile.exists()) {
			properties = _readProperties(propertiesFile);
		}

		if (Validator.isNull(content)) {
			_write(propertiesFile, content);

			return;
		}

		Properties parentProperties = null;

		if (parentLanguageId != null) {
			File parentPropertiesFile = new File(
				StringBundler.concat(
					_langDirName, "/", _langFileName, "_", parentLanguageId,
					".properties"));

			if (parentPropertiesFile.exists()) {
				parentProperties = _readProperties(parentPropertiesFile);
			}
		}

		StringBundler sb = new StringBundler();

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				line = line.trim();

				String[] array = line.split("=", 2);

				if (array.length != 2) {
					sb.append(line);
					sb.append("\n");

					continue;
				}

				String key = array[0];

				String translatedText = properties.getProperty(key);

				if (_keysWithUpdatedValues.contains(key)) {
					translatedText = null;
				}

				if ((translatedText != null) && (parentProperties != null) &&
					translatedText.endsWith(
						LanguageBuilderUtil.AUTOMATIC_COPY)) {

					translatedText = null;
				}

				boolean inheritedFromParent = false;

				if ((translatedText == null) && (parentProperties != null)) {
					inheritedFromParent = true;
					translatedText = parentProperties.getProperty(key);
				}

				if ((translatedText == null) && (_renameKeys != null)) {
					String renameKey = _renameKeys.getProperty(key);

					if (renameKey != null) {
						translatedText = properties.getProperty(key);

						if ((translatedText == null) &&
							(parentProperties != null)) {

							inheritedFromParent = true;
							translatedText = parentProperties.getProperty(key);
						}
					}
				}

				boolean automaticCopy = false;

				if (!inheritedFromParent && (translatedText != null) &&
					translatedText.endsWith(
						LanguageBuilderUtil.AUTOMATIC_COPY)) {

					automaticCopy = true;
					translatedText = "";
				}

				if ((translatedText == null) || translatedText.equals("")) {
					String value = array[1];

					if (LanguageValidator.isSpecialPropertyKey(key)) {
						translatedText = _getSpecialPropertyValue(key);
					}
					else if (line.contains("{") || line.contains("<") ||
							 ArrayUtil.contains(
								 _AUTOMATIC_COPY_LANGUAGE_IDS, languageId)) {

						translatedText =
							value + LanguageBuilderUtil.AUTOMATIC_COPY;
					}
					else if (line.contains("[")) {
						translatedText =
							value + LanguageBuilderUtil.AUTOMATIC_COPY;
					}
					else if (!automaticCopy && key.endsWith("-delimiter")) {
						translatedText = "";
					}
					else if (languageId.equals("el") &&
							 (key.equals("enabled") || key.equals("on") ||
							  key.equals("on-date"))) {

						translatedText = "";
					}
					else if (languageId.equals("es") && key.equals("am")) {
						translatedText = "";
					}
					else if (languageId.equals("fi") &&
							 (key.equals("on") || key.equals("the"))) {

						translatedText = "";
					}
					else if (languageId.equals("it") && key.equals("am")) {
						translatedText = "";
					}
					else if (languageId.equals("ja") &&
							 (key.equals("any") || key.equals("anytime") ||
							  key.equals("down") || key.equals("on") ||
							  key.equals("on-date") || key.equals("the"))) {

						translatedText = "";
					}
					else if (languageId.equals("ko") && key.equals("the")) {
						translatedText = "";
					}
					else {
						translatedText = _translate(
							"en", languageId, key, value, 0);

						if (Validator.isNull(translatedText)) {
							translatedText =
								value + LanguageBuilderUtil.AUTOMATIC_COPY;
						}
						else if (!key.startsWith("country.") &&
								 !key.startsWith("language.")) {

							translatedText =
								translatedText +
									LanguageBuilderUtil.AUTOMATIC_TRANSLATION;
						}
					}
				}

				if (Validator.isNotNull(translatedText) ||
					key.endsWith("-delimiter")) {

					translatedText = _fixTranslation(translatedText);

					sb.append(key);
					sb.append(StringPool.EQUAL);
					sb.append(translatedText);
					sb.append("\n");
				}
			}
		}

		sb.setIndex(sb.index() - 1);

		content = sb.toString();

		_write(propertiesFile, content);
	}

	private String _fixContraction(
		String s, String contraction, String replacement) {

		int i = s.indexOf(contraction);

		if ((i == -1) ||
			((i > 0) && Character.isLetterOrDigit(s.charAt(i - 1))) ||
			(((i + contraction.length()) < s.length()) &&
			 Character.isLetterOrDigit(s.charAt(i + contraction.length())))) {

			return s;
		}

		return StringUtil.replaceFirst(s, contraction, replacement, i);
	}

	private String _fixEnglishTranslation(String key, String value) {

		// http://en.wikibooks.org/wiki/Basic_Book_Design/Capitalizing_Words_in_Titles
		// http://titlecapitalization.com
		// http://www.imdb.com

		if (value.contains(" this ")) {
			if (value.contains(".") || value.contains("?") ||
				value.contains(":") ||
				key.equals(
					"the-url-of-the-page-comparing-this-page-content-with-" +
						"the-previous-version")) {
			}
			else {
				value = StringUtil.replace(value, " this ", " This ");
			}
		}
		else {
			value = StringUtil.replace(value, " From ", " from ");
		}

		return value;
	}

	private String _fixTranslation(String value) {
		value = StringUtil.replace(value, "\n", "\\n");
		value = StringUtil.replace(
			value, CharPool.NO_BREAK_SPACE, CharPool.SPACE);

		value = StringUtil.replace(
			value.trim(),
			new String[] {
				"  ", "<b>", "</b>", "<i>", "</i>", " url ", "&#39;", "&#39 ;",
				"&quot;", "&quot ;", "ReCaptcha", "Captcha"
			},
			new String[] {
				" ", "<strong>", "</strong>", "<em>", "</em>", " URL ", "\'",
				"\'", "\"", "\"", "reCAPTCHA", "CAPTCHA"
			});
		value = StringUtil.replace(
			value.trim(),
			new char[] {
				'\u2018', '\u2019', '\u201a', '\u201b', '\u201c', '\u201d',
				'\u201e', '\u201f'
			},
			new char[] {'\'', '\'', '\'', '\'', '\"', '\"', '\"', '\"'});

		for (String[] contractionArray : _CONTRACTIONS) {
			String contraction = contractionArray[0];
			String replacement = contractionArray[1];

			value = _fixContraction(value, contraction, replacement);

			if (!contraction.startsWith("I'")) {
				value = _fixContraction(
					value,
					Character.toLowerCase(contraction.charAt(0)) +
						contraction.substring(1),
					Character.toLowerCase(replacement.charAt(0)) +
						replacement.substring(1));
			}
		}

		return value;
	}

	private LangBuilderCategory _getLangBuilderCategory(
		String key, boolean useSingleCategory) {

		LangBuilderCategory defaultCategory = LangBuilderCategory.MESSAGES;

		if (useSingleCategory) {
			return defaultCategory;
		}

		for (LangBuilderCategory langBuilderCategory :
				LangBuilderCategory.values()) {

			if (Validator.isNotNull(langBuilderCategory.getPrefix()) &&
				key.startsWith(langBuilderCategory.getPrefix())) {

				return langBuilderCategory;
			}
		}

		return defaultCategory;
	}

	private String _getMicrosoftLanguageId(String languageId) {
		if (languageId.equals("pt_BR") || languageId.equals("pt_PT")) {
			return "pt";
		}
		else if (languageId.equals("hi_IN")) {
			return "hi";
		}
		else if (languageId.equals("in")) {
			return "id";
		}
		else if (languageId.equals("iw")) {
			return "he";
		}
		else if (languageId.equals("nb")) {
			return "no";
		}
		else if (languageId.equals("zh_CN")) {
			return "zh-CHS";
		}
		else if (languageId.equals("zh_TW")) {
			return "zh-CHT";
		}

		return languageId;
	}

	private String _getSpecialPropertyValue(String key) {
		if (key.equals(LanguageConstants.KEY_DIR)) {
			return LanguageConstants.VALUE_LTR;
		}
		else if (key.equals(LanguageConstants.KEY_LINE_BEGIN)) {
			return LanguageConstants.VALUE_LEFT;
		}
		else if (key.equals(LanguageConstants.KEY_LINE_END)) {
			return LanguageConstants.VALUE_RIGHT;
		}

		return StringPool.BLANK;
	}

	private void _initKeysWithUpdatedValues() throws Exception {
		File backupLanguageFile = new File(
			StringBundler.concat(
				_langDirName, "/", _langFileName, "_en.properties"));

		if (!backupLanguageFile.exists()) {
			return;
		}

		Properties backupLanguageProperties = _readProperties(
			backupLanguageFile);

		File languageFile = new File(
			StringBundler.concat(
				_langDirName, "/", _langFileName, ".properties"));

		Properties languageProperties = _readProperties(languageFile);

		Set<Map.Entry<Object, Object>> set = languageProperties.entrySet();

		for (Map.Entry<Object, Object> entry : set) {
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();

			if (!value.equals(backupLanguageProperties.get(key))) {
				_keysWithUpdatedValues.add(key);
			}
		}
	}

	private String _orderProperties(File propertiesFile, boolean checkExistence)
		throws Exception {

		if (checkExistence && !propertiesFile.exists()) {
			_write(propertiesFile, StringPool.BLANK);

			return StringPool.BLANK;
		}

		boolean useSingleCategory = true;

		String absolutePath = StringUtil.replace(
			propertiesFile.getAbsolutePath(), CharPool.BACK_SLASH,
			CharPool.SLASH);

		if (absolutePath.contains("/portal-impl/src/content/")) {
			useSingleCategory = false;
		}

		String content = _read(propertiesFile);

		Map<LangBuilderCategory, Map<String, String>> messages = new TreeMap<>(
			new LangBuilderCategoryComparator());

		try (UnsyncBufferedReader unsyncBufferedReader =
				new UnsyncBufferedReader(new UnsyncStringReader(content))) {

			String line = null;

			while ((line = unsyncBufferedReader.readLine()) != null) {
				String[] array = line.split("=", 2);

				if (array.length != 2) {
					continue;
				}

				String key = array[0];
				String value = array[1];

				if (Validator.isNull(key) || Validator.isNull(value)) {
					continue;
				}

				value = _fixTranslation(value);

				if (_titleCapitalization) {
					value = _fixEnglishTranslation(key, value);
				}

				_addMessage(messages, key, value, useSingleCategory);
			}
		}

		if (messages.isEmpty()) {
			return StringPool.BLANK;
		}

		StringBundler sb = new StringBundler();

		for (Map.Entry<LangBuilderCategory, Map<String, String>> entry1 :
				messages.entrySet()) {

			if (!useSingleCategory) {
				LangBuilderCategory langBuilderCategory = entry1.getKey();

				sb.append("##\n");
				sb.append("## ");
				sb.append(langBuilderCategory.getDescription());
				sb.append("\n");
				sb.append("##\n\n");
			}

			Map<String, String> categoryMessages = entry1.getValue();

			for (Map.Entry<String, String> entry2 :
					categoryMessages.entrySet()) {

				sb.append(entry2.getKey());
				sb.append(StringPool.EQUAL);
				sb.append(entry2.getValue());
				sb.append("\n");
			}

			sb.append("\n");
		}

		sb.setIndex(sb.index() - 2);

		content = sb.toString();

		_write(propertiesFile, content);

		return content;
	}

	private String _read(File file) throws Exception {
		String s = new String(
			Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

		return StringUtil.replace(
			s, StringPool.RETURN_NEW_LINE, StringPool.NEW_LINE);
	}

	private Properties _readProperties(File file) throws Exception {
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
			return PropertiesUtil.load(fileInputStream, StringPool.UTF8);
		}
	}

	private String _translate(
		String fromLanguageId, String toLanguageId, String key, String fromText,
		int limit) {

		if (!_translate) {
			return null;
		}

		// LPS-61961

		if (ArrayUtil.contains(_excludedLanguageIds, toLanguageId)) {
			return null;
		}

		// Limit the number of retries to 3

		if (limit == 3) {
			return null;
		}

		Language toLanguage = Language.fromString(
			_getMicrosoftLanguageId(toLanguageId));

		if (toLanguage == null) {
			return null;
		}

		Language fromLanguage = Language.fromString(
			_getMicrosoftLanguageId(fromLanguageId));

		String toText = null;

		try {
			System.out.println(
				StringBundler.concat(
					"Translating ", fromLanguageId, "_", toLanguageId, " ", key,
					" ", fromText));

			toText = Translate.execute(fromText, fromLanguage, toLanguage);
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}

		// Keep trying

		if (toText == null) {
			return _translate(
				fromLanguageId, toLanguageId, key, fromText, ++limit);
		}

		return toText;
	}

	private void _write(File file, String s) throws Exception {
		FileUtils.writeStringToFile(file, s, StringPool.UTF8);
	}

	private static final String[] _AUTOMATIC_COPY_LANGUAGE_IDS = {
		"en_AU", "en_GB", "fr_CA"
	};

	private static final String[][] _CONTRACTIONS = {
		{"Aren't", "Are not"}, {"Can't", "Cannot"}, {"Could've", "Could have"},
		{"Couldn't", "Could not"}, {"Didn't", "Did not"},
		{"Doesn't", "Does not"}, {"Don't", "Do not"}, {"Hadn't", "Had not"},
		{"Hasn't", "Has not"}, {"Haven't", "Have not"}, {"How's", "How is"},
		{"I'd", "I would"}, {"I'll", "I will"}, {"I've", "I have"},
		{"Isn't", "Is not"}, {"It's", "It is"}, {"Let's", "Let us"},
		{"Shouldn't", "Should not"}, {"That's", "That is"},
		{"There's", "There is"}, {"Wasn't", "Was not"}, {"We'd", "We would"},
		{"We'll", "We will"}, {"We're", "We are"}, {"We've", "We have"},
		{"Weren't", "Were not"}, {"What's", "What is"}, {"Where's", "Where is"},
		{"Would've", "Would have"}, {"Wouldn't", "Would not"},
		{"You'd", "You would"}, {"You'll", "You will"}, {"You're", "You are"},
		{"You've", "You have"}
	};

	private final String[] _excludedLanguageIds;
	private final Set<String> _keysWithUpdatedValues = new HashSet<>();
	private final String _langDirName;
	private final String _langFileName;
	private final Properties _renameKeys;
	private final boolean _titleCapitalization;
	private final boolean _translate;

}