/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.reports.web.internal.model;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @author Cristina González
 */
public class LayoutReportsIssue {

	public LayoutReportsIssue(List<Detail> details, Key key) {
		if (key == null) {
			throw new IllegalArgumentException("Key is null");
		}

		if (details == null) {
			_details = Collections.emptyList();
		}
		else {
			_details = Collections.unmodifiableList(details);
		}

		_key = key;

		long total = 0;

		for (LayoutReportsIssue.Detail detail : _details) {
			total += detail.getTotal();
		}

		_total = total;
	}

	@Override
	public boolean equals(Object object) {
		LayoutReportsIssue layoutReportsIssue = (LayoutReportsIssue)object;

		if (Objects.equals(layoutReportsIssue._details, _details) &&
			Objects.equals(layoutReportsIssue._key, _key) &&
			(layoutReportsIssue._total == _total)) {

			return true;
		}

		return false;
	}

	public List<Detail> getDetails() {
		return _details;
	}

	public Key getKey() {
		return _key;
	}

	public long getTotal() {
		return _total;
	}

	@Override
	public int hashCode() {
		int hashCode = HashUtil.hash(0, _key);

		return HashUtil.hash(hashCode, _total);
	}

	public JSONObject toJSONObject(
			String configureLayoutSeoURL, String configurePagesSeoURL,
			ResourceBundle resourceBundle)
		throws Exception {

		return JSONUtil.put(
			"details",
			JSONUtil.toJSONArray(
				_details,
				detail -> {
					if (detail.getTotal() <= 0) {
						return null;
					}

					return detail.toJSONObject(
						configureLayoutSeoURL, configurePagesSeoURL,
						resourceBundle);
				})
		).put(
			"key", _key.toString()
		).put(
			"title",
			ResourceBundleUtil.getString(resourceBundle, _key.toString())
		).put(
			"total", _total
		);
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler((7 * _details.size()) + 3);

		sb.append("{details={");

		for (int i = 0; i < _details.size(); i++) {
			Detail detail = _details.get(i);

			if (detail.getTotal() <= 0) {
				continue;
			}

			sb.append(detail.getKey());
			sb.append("=");
			sb.append(detail.getTotal());

			if (i >= (_details.size() - 1)) {
				continue;
			}

			sb.append(", ");
		}

		sb.append("}, key=");
		sb.append(_key);
		sb.append(", total=");
		sb.append(_total);
		sb.append("}");

		return sb.toString();
	}

	public static class Detail {

		public Detail(Key key, JSONObject lighthouseAuditJSONObject) {
			if (key == null) {
				throw new IllegalArgumentException("Key is null");
			}

			_key = key;
			_lighthouseAuditJSONObject = lighthouseAuditJSONObject;

			_total = _calculateTotal();
		}

		@Override
		public boolean equals(Object object) {
			Detail detail = (Detail)object;

			if ((detail._key == _key) && (detail._total == _total)) {
				return true;
			}

			return false;
		}

		public Key getKey() {
			return _key;
		}

		public long getTotal() {
			return _total;
		}

		@Override
		public int hashCode() {
			int hashCode = HashUtil.hash(0, _key);

			return HashUtil.hash(hashCode, _total);
		}

		public JSONObject toJSONObject(
			String configureLayoutSeoURL, String configurePagesSeoURL,
			ResourceBundle resourceBundle) {

			return JSONUtil.put(
				"description", _key.getDescription(resourceBundle)
			).put(
				"failingElements",
				_key.getFailingElementsJSONArray(
					configureLayoutSeoURL, configurePagesSeoURL,
					_lighthouseAuditJSONObject, resourceBundle)
			).put(
				"key", _key.toString()
			).put(
				"tips", _key.getTips(resourceBundle)
			).put(
				"title", _key.getTitle(resourceBundle)
			).put(
				"total", _total
			);
		}

		@Override
		public String toString() {
			JSONObject jsonObject = toJSONObject(
				null, null, ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE);

			return jsonObject.toString();
		}

		public enum Key {

			ILLEGIBLE_FONT_SIZES {

				@Override
				public String toString() {
					return "illegible-font-sizes";
				}

				@Override
				protected String[] getDescriptionArguments(
					ResourceBundle resourceBundle) {

					return new String[] {
						getLearnMoreLink(
							resourceBundle, "https://web.dev/font-size")
					};
				}

				@Override
				protected JSONArray getFailingElementsJSONArray(
					String configureLayoutSeoURL, String configurePagesSeoURL,
					JSONObject lighthouseAuditJSONObject,
					ResourceBundle resourceBundle) {

					JSONArray failingElementsJSONArray =
						super.getFailingElementsJSONArray(
							configureLayoutSeoURL, configurePagesSeoURL,
							lighthouseAuditJSONObject, resourceBundle);

					if (failingElementsJSONArray == null) {
						return null;
					}

					JSONArray filteredFailingElementsJSONArray =
						JSONFactoryUtil.createJSONArray();

					for (int i = 0; i < failingElementsJSONArray.length();
						 i++) {

						JSONObject failingElementJSONObject =
							failingElementsJSONArray.getJSONObject(i);

						if (failingElementJSONObject == null) {
							continue;
						}

						JSONObject selectorJSONObject =
							failingElementJSONObject.getJSONObject("selector");

						if (selectorJSONObject == null) {
							continue;
						}

						filteredFailingElementsJSONArray.put(
							failingElementJSONObject);
					}

					return filteredFailingElementsJSONArray;
				}

			},
			INCORRECT_IMAGE_ASPECT_RATIOS {

				@Override
				public String toString() {
					return "incorrect-image-aspect-ratios";
				}

				@Override
				protected String[] getDescriptionArguments(
					ResourceBundle resourceBundle) {

					return new String[] {
						getLearnMoreLink(
							resourceBundle,
							"https://web.dev/image-aspect-ratio")
					};
				}

			},
			INVALID_CANONICAL_URL {

				@Override
				public String toString() {
					return "invalid-canonical-url";
				}

				@Override
				protected String[] getDescriptionArguments(
					ResourceBundle resourceBundle) {

					return new String[] {
						getLearnMoreLink(
							resourceBundle, "https://web.dev/canonical")
					};
				}

				@Override
				protected JSONArray getFailingElementsJSONArray(
					String configureLayoutSeoURL, String configurePagesSeoURL,
					JSONObject lighthouseAuditJSONObject,
					ResourceBundle resourceBundle) {

					return JSONUtil.putAll(
						JSONUtil.put(
							"content",
							LanguageUtil.format(
								resourceBundle,
								getDetailLanguageKey() + "-failing-element",
								_getLinkArguments(configurePagesSeoURL))));
				}

			},
			INVALID_HREFLANG {

				@Override
				public String toString() {
					return "invalid-hreflang";
				}

				@Override
				protected String[] getDescriptionArguments(
					ResourceBundle resourceBundle) {

					return new String[] {
						getLearnMoreLink(
							resourceBundle, "https://web.dev/hreflang")
					};
				}

			},
			LINK_TEXTS {

				@Override
				public String toString() {
					return "link-texts";
				}

				@Override
				protected String[] getDescriptionArguments(
					ResourceBundle resourceBundle) {

					return new String[] {
						getLearnMoreLink(
							resourceBundle, "https://web.dev/link-text")
					};
				}

			},
			LOW_CONTRAST_RATIO {

				@Override
				public String toString() {
					return "low-contrast-ratio";
				}

				@Override
				protected String[] getDescriptionArguments(
					ResourceBundle resourceBundle) {

					return new String[] {
						getLearnMoreLink(
							resourceBundle, "https://web.dev/color-contrast")
					};
				}

			},
			MISSING_IMG_ALT_ATTRIBUTES {

				@Override
				public String toString() {
					return "missing-img-alt-attributes";
				}

				@Override
				protected String[] getDescriptionArguments(
					ResourceBundle resourceBundle) {

					return new String[] {
						getLearnMoreLink(
							resourceBundle, "https://web.dev/image-alt")
					};
				}

			},
			MISSING_INPUT_ALT_ATTRIBUTES {

				@Override
				public String toString() {
					return "missing-input-alt-attributes";
				}

				@Override
				protected String[] getDescriptionArguments(
					ResourceBundle resourceBundle) {

					return new String[] {
						getHtmlCode("<input>"),
						getLearnMoreLink(
							resourceBundle, "https://web.dev/input-image-alt")
					};
				}

			},
			MISSING_META_DESCRIPTION {

				@Override
				public String toString() {
					return "missing-meta-description";
				}

				@Override
				protected String[] getDescriptionArguments(
					ResourceBundle resourceBundle) {

					return new String[] {
						getLearnMoreLink(
							resourceBundle, "https://web.dev/meta-description")
					};
				}

				@Override
				protected JSONArray getFailingElementsJSONArray(
					String configureLayoutSeoURL, String configurePagesSeoURL,
					JSONObject lighthouseAuditJSONObject,
					ResourceBundle resourceBundle) {

					return JSONUtil.putAll(
						JSONUtil.put(
							"content",
							LanguageUtil.format(
								resourceBundle,
								getDetailLanguageKey() + "-failing-element",
								_getLinkArguments(configureLayoutSeoURL))));
				}

			},
			MISSING_TITLE_ELEMENT {

				@Override
				protected String[] getTitleArguments(
					ResourceBundle resourceBundle) {

					return new String[] {"<title>"};
				}

				@Override
				protected String[] getTipsArguments(
					ResourceBundle resourceBundle) {

					return new String[] {getHtmlCode("<title>")};
				}

				@Override
				public String toString() {
					return "missing-title-element";
				}

				@Override
				protected String[] getDescriptionArguments(
					ResourceBundle resourceBundle) {

					return new String[] {
						getLearnMoreLink(
							resourceBundle, "https://web.dev/document-title")
					};
				}

				@Override
				protected JSONArray getFailingElementsJSONArray(
					String configureLayoutSeoURL, String configurePagesSeoURL,
					JSONObject lighthouseAuditJSONObject,
					ResourceBundle resourceBundle) {

					return JSONUtil.putAll(
						JSONUtil.put(
							"content",
							LanguageUtil.format(
								resourceBundle,
								getDetailLanguageKey() + "-failing-element",
								_getLinkArguments(configureLayoutSeoURL))));
				}

			},
			NOT_ALL_LINKS_ARE_CRAWLABLE {

				@Override
				public String toString() {
					return "not-all-links-are-crawlable";
				}

				@Override
				protected String[] getTipsArguments(
					ResourceBundle resourceBundle) {

					return new String[] {
						getHtmlCode("<a>"), getHtmlCode("href"),
						getHtmlCode("<a href=\"https://example.com\">")
					};
				}

				@Override
				protected String[] getDescriptionArguments(
					ResourceBundle resourceBundle) {

					return new String[] {
						getHtmlCode("href"),
						getLearnMoreLink(
							resourceBundle,
							"https://support.google.com/webmasters/answer" +
								"/9112205")
					};
				}

			},
			PAGE_BLOCKED_FROM_INDEXING {

				@Override
				public String toString() {
					return "page-blocked-from-indexing";
				}

				@Override
				protected String[] getTipsArguments(
					ResourceBundle resourceBundle) {

					return new String[] {getHtmlCode("noindex")};
				}

				@Override
				protected String[] getDescriptionArguments(
					ResourceBundle resourceBundle) {

					return new String[] {
						getLearnMoreLink(
							resourceBundle, "https://web.dev/is-crawlable")
					};
				}

				@Override
				protected JSONArray getFailingElementsJSONArray(
					String configureLayoutSeoURL, String configurePagesSeoURL,
					JSONObject lighthouseAuditJSONObject,
					ResourceBundle resourceBundle) {

					return JSONUtil.putAll(
						JSONUtil.put(
							"content",
							LanguageUtil.format(
								resourceBundle,
								getDetailLanguageKey() + "-failing-element",
								_getLinkArguments(configureLayoutSeoURL))));
				}

			},
			SMALL_TAP_TARGETS {

				@Override
				public String toString() {
					return "small-tap-targets";
				}

				@Override
				protected String[] getDescriptionArguments(
					ResourceBundle resourceBundle) {

					return new String[] {
						getLearnMoreLink(
							resourceBundle, "https://web.dev/tap-targets")
					};
				}

			};

			public String getDescription(ResourceBundle resourceBundle) {
				return LanguageUtil.format(
					resourceBundle, getDetailLanguageKey() + "-description",
					getDescriptionArguments(resourceBundle), false);
			}

			public String getTips(ResourceBundle resourceBundle) {
				return ResourceBundleUtil.getString(
					resourceBundle, getDetailLanguageKey() + "-tip",
					getTipsArguments(resourceBundle));
			}

			public String getTitle(ResourceBundle resourceBundle) {
				return LanguageUtil.format(
					resourceBundle, getDetailLanguageKey(),
					getTitleArguments(resourceBundle), false);
			}

			protected String[] getDescriptionArguments(
				ResourceBundle resourceBundle) {

				return new String[0];
			}

			protected String getDetailLanguageKey() {
				return "detail-" + toString();
			}

			protected JSONArray getFailingElementsJSONArray(
				String configureLayoutSeoURL, String configurePagesSeoURL,
				JSONObject lighthouseAuditJSONObject,
				ResourceBundle resourceBundle) {

				JSONObject detailsJSONObject =
					lighthouseAuditJSONObject.getJSONObject("details");

				if (detailsJSONObject != null) {
					return detailsJSONObject.getJSONArray("items");
				}

				return null;
			}

			protected String getHtmlCode(String html) {
				return "<code>" + HtmlUtil.escape(html) + "</code>";
			}

			protected String getLearnMoreLink(
				ResourceBundle resourceBundle, String url) {

				return getLink(
					LanguageUtil.format(
						resourceBundle, "learn-more-about-x",
						HtmlUtil.escape(getTitle(resourceBundle)), false),
					url);
			}

			protected String getLink(String content, String url) {
				return StringBundler.concat(
					"<a href=\"", url, "\" target=\"_blank\">", content,
					"</a>");
			}

			protected String[] getTipsArguments(ResourceBundle resourceBundle) {
				return new String[0];
			}

			protected String[] getTitleArguments(
				ResourceBundle resourceBundle) {

				return new String[0];
			}

		}

		private int _calculateTotal() {
			if (Objects.equals(
					_lighthouseAuditJSONObject.getString("scoreDisplayMode"),
					"notApplicable")) {

				return 0;
			}

			float score = GetterUtil.getFloat(
				_lighthouseAuditJSONObject.get("score"));

			if (score == 1) {
				return 0;
			}

			JSONArray failingElementsJSONArray =
				_key.getFailingElementsJSONArray(
					null, null, _lighthouseAuditJSONObject,
					ResourceBundleUtil.EMPTY_RESOURCE_BUNDLE);

			if ((failingElementsJSONArray != null) &&
				(failingElementsJSONArray.length() > 0)) {

				return failingElementsJSONArray.length();
			}

			if (score == 0) {
				return 1;
			}

			return 0;
		}

		private final Detail.Key _key;
		private final JSONObject _lighthouseAuditJSONObject;
		private final long _total;

	}

	public enum Key {

		ACCESSIBILITY {

			@Override
			public String toString() {
				return "accessibility";
			}

		},
		SEO {

			@Override
			public String toString() {
				return "seo";
			}

		},

	}

	private static String[] _getLinkArguments(String url) {
		if (Validator.isNotNull(url)) {
			return new String[] {"<a href=\"" + url + "\">", "</a>"};
		}

		return new String[] {StringPool.BLANK, StringPool.BLANK};
	}

	private final List<Detail> _details;
	private final Key _key;
	private final long _total;

}