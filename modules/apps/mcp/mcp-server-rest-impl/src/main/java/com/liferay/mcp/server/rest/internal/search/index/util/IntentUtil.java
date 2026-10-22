/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Petteri Karttunen
 */
public class IntentUtil {

	public static final String INTENT_COPY = "copy";

	public static final String INTENT_CREATE = "create";

	public static final String INTENT_DELETE = "delete";

	public static final String INTENT_DEPLOY = "deploy";

	public static final String INTENT_EXPORT = "export";

	public static final String INTENT_LIST = "list";

	public static final String INTENT_MOVE = "move";

	public static final String INTENT_PUBLISH = "publish";

	public static final String INTENT_READ = "read";

	public static final String INTENT_REPLACE = "replace";

	public static final String INTENT_RESTORE = "restore";

	public static final String INTENT_SUBSCRIBE = "subscribe";

	public static final String INTENT_TRANSLATE = "translate";

	public static final String INTENT_UNDEPLOY = "undeploy";

	public static final String INTENT_UNPUBLISH = "unpublish";

	public static final String INTENT_UNSUBSCRIBE = "unsubscribe";

	public static final String INTENT_UPDATE = "update";

	public static final String INTENT_VALIDATE = "validate";

	public static Set<String> getIntents(String verb) {
		return SetUtil.fromList(
			_verbIntents.getOrDefault(verb, Collections.emptyList()));
	}

	public static Set<String> getOtherIntents(String verb) {
		Set<String> otherIntents = new TreeSet<>();

		Set<String> intents = getIntents(verb);

		for (List<String> verbIntents : _verbIntents.values()) {
			for (String intent : verbIntents) {
				if (intents.contains(intent)) {
					continue;
				}

				otherIntents.add(intent);
			}
		}

		return otherIntents;
	}

	public static Set<String> getPrimaryVerbs(String intent) {
		Set<String> primaryVerbs = new TreeSet<>();

		for (Map.Entry<String, List<String>> entry : _verbIntents.entrySet()) {
			List<String> intents = entry.getValue();

			if (!Objects.equals(intents.getFirst(), intent)) {
				continue;
			}

			primaryVerbs.add(entry.getKey());
		}

		return primaryVerbs;
	}

	public static Set<String> getToolActions() {
		return _toolActionIntents.keySet();
	}

	public static String getToolIntent(
		boolean collection, String method, String toolAction) {

		if (toolAction != null) {
			return _toolActionIntents.getOrDefault(
				toolAction, StringPool.BLANK);
		}

		if (Objects.equals(method, "get")) {
			if (collection) {
				return INTENT_LIST;
			}

			return INTENT_READ;
		}

		return _methodIntents.getOrDefault(method, StringPool.BLANK);
	}

	public static String getVerb(String[] words) {
		for (int length = words.length; length > 0; length--) {
			for (int i = 0; (i + length) <= words.length; i++) {
				String verb = StringUtil.merge(
					Arrays.copyOfRange(words, i, i + length), StringPool.SPACE);

				if (isVerb(verb)) {
					return verb;
				}
			}
		}

		return null;
	}

	public static boolean isVerb(String s) {
		return _verbIntents.containsKey(s);
	}

	private static final Map<String, String> _methodIntents =
		HashMapBuilder.put(
			"delete", INTENT_DELETE
		).put(
			"patch", INTENT_UPDATE
		).put(
			"post", INTENT_CREATE
		).put(
			"put", INTENT_REPLACE
		).build();
	private static final Map<String, String> _toolActionIntents =
		HashMapBuilder.put(
			"Copy", INTENT_COPY
		).put(
			"CopyReplace", INTENT_COPY
		).put(
			"Deploy", INTENT_DEPLOY
		).put(
			"Expire", INTENT_UNPUBLISH
		).put(
			"ExportBatch", INTENT_EXPORT
		).put(
			"Move", INTENT_MOVE
		).put(
			"MoveReplace", INTENT_MOVE
		).put(
			"Publish", INTENT_PUBLISH
		).put(
			"Restore", INTENT_RESTORE
		).put(
			"Subscribe", INTENT_SUBSCRIBE
		).put(
			"Translation", INTENT_TRANSLATE
		).put(
			"TranslationLanguage", INTENT_TRANSLATE
		).put(
			"Undeploy", INTENT_UNDEPLOY
		).put(
			"Unsubscribe", INTENT_UNSUBSCRIBE
		).put(
			"Validate", INTENT_VALIDATE
		).build();
	private static final Map<String, List<String>> _verbIntents =
		HashMapBuilder.<String, List<String>>put(
			"activate",
			Arrays.asList(INTENT_PUBLISH, INTENT_DEPLOY, INTENT_UPDATE)
		).put(
			"add", Arrays.asList(INTENT_CREATE)
		).put(
			"assign", Arrays.asList(INTENT_CREATE)
		).put(
			"associate", Arrays.asList(INTENT_CREATE)
		).put(
			"attach", Arrays.asList(INTENT_CREATE)
		).put(
			"browse", Arrays.asList(INTENT_LIST, INTENT_READ)
		).put(
			"change", Arrays.asList(INTENT_UPDATE, INTENT_REPLACE)
		).put(
			"check", Arrays.asList(INTENT_VALIDATE, INTENT_LIST, INTENT_READ)
		).put(
			"clone", Arrays.asList(INTENT_COPY)
		).put(
			"copy", Arrays.asList(INTENT_COPY)
		).put(
			"create", Arrays.asList(INTENT_CREATE)
		).put(
			"deactivate", Arrays.asList(INTENT_UNDEPLOY, INTENT_UPDATE)
		).put(
			"delete", Arrays.asList(INTENT_DELETE)
		).put(
			"deploy", Arrays.asList(INTENT_DEPLOY)
		).put(
			"destroy", Arrays.asList(INTENT_DELETE)
		).put(
			"detach", Arrays.asList(INTENT_DELETE)
		).put(
			"disassociate", Arrays.asList(INTENT_DELETE)
		).put(
			"duplicate", Arrays.asList(INTENT_COPY)
		).put(
			"edit", Arrays.asList(INTENT_UPDATE, INTENT_REPLACE)
		).put(
			"expire", Arrays.asList(INTENT_UNPUBLISH)
		).put(
			"export", Arrays.asList(INTENT_EXPORT)
		).put(
			"fetch", Arrays.asList(INTENT_READ, INTENT_LIST)
		).put(
			"find", Arrays.asList(INTENT_LIST, INTENT_READ)
		).put(
			"follow", Arrays.asList(INTENT_SUBSCRIBE)
		).put(
			"get", Arrays.asList(INTENT_READ, INTENT_LIST)
		).put(
			"get all", Arrays.asList(INTENT_LIST)
		).put(
			"get rid of", Arrays.asList(INTENT_DELETE)
		).put(
			"import", Arrays.asList(INTENT_CREATE)
		).put(
			"link", Arrays.asList(INTENT_CREATE)
		).put(
			"list", Arrays.asList(INTENT_LIST, INTENT_READ)
		).put(
			"localize", Arrays.asList(INTENT_TRANSLATE)
		).put(
			"look up", Arrays.asList(INTENT_READ, INTENT_LIST)
		).put(
			"make", Arrays.asList(INTENT_CREATE)
		).put(
			"make live", Arrays.asList(INTENT_PUBLISH)
		).put(
			"modify", Arrays.asList(INTENT_UPDATE, INTENT_REPLACE)
		).put(
			"move", Arrays.asList(INTENT_MOVE)
		).put(
			"open", Arrays.asList(INTENT_READ, INTENT_LIST)
		).put(
			"overwrite", Arrays.asList(INTENT_REPLACE)
		).put(
			"patch", Arrays.asList(INTENT_UPDATE)
		).put(
			"place", Arrays.asList(INTENT_CREATE)
		).put(
			"post", Arrays.asList(INTENT_CREATE)
		).put(
			"publish", Arrays.asList(INTENT_PUBLISH)
		).put(
			"put", Arrays.asList(INTENT_REPLACE)
		).put(
			"read", Arrays.asList(INTENT_READ, INTENT_LIST)
		).put(
			"recover", Arrays.asList(INTENT_RESTORE)
		).put(
			"relate", Arrays.asList(INTENT_CREATE)
		).put(
			"relocate", Arrays.asList(INTENT_MOVE)
		).put(
			"remove", Arrays.asList(INTENT_DELETE)
		).put(
			"rename", Arrays.asList(INTENT_UPDATE, INTENT_REPLACE)
		).put(
			"replace", Arrays.asList(INTENT_REPLACE)
		).put(
			"restore", Arrays.asList(INTENT_RESTORE)
		).put(
			"retire", Arrays.asList(INTENT_UNPUBLISH)
		).put(
			"retrieve", Arrays.asList(INTENT_READ, INTENT_LIST)
		).put(
			"see", Arrays.asList(INTENT_LIST, INTENT_READ)
		).put(
			"set", Arrays.asList(INTENT_REPLACE, INTENT_UPDATE)
		).put(
			"set up", Arrays.asList(INTENT_CREATE)
		).put(
			"show", Arrays.asList(INTENT_LIST, INTENT_READ)
		).put(
			"sign up", Arrays.asList(INTENT_CREATE)
		).put(
			"start", Arrays.asList(INTENT_CREATE)
		).put(
			"submit", Arrays.asList(INTENT_CREATE)
		).put(
			"subscribe", Arrays.asList(INTENT_SUBSCRIBE)
		).put(
			"transfer", Arrays.asList(INTENT_MOVE)
		).put(
			"translate", Arrays.asList(INTENT_TRANSLATE)
		).put(
			"unassign", Arrays.asList(INTENT_DELETE)
		).put(
			"undelete", Arrays.asList(INTENT_RESTORE)
		).put(
			"undeploy", Arrays.asList(INTENT_UNDEPLOY)
		).put(
			"unfollow", Arrays.asList(INTENT_UNSUBSCRIBE)
		).put(
			"unlink", Arrays.asList(INTENT_DELETE)
		).put(
			"unpublish", Arrays.asList(INTENT_UNPUBLISH)
		).put(
			"unsubscribe", Arrays.asList(INTENT_UNSUBSCRIBE)
		).put(
			"unwatch", Arrays.asList(INTENT_UNSUBSCRIBE)
		).put(
			"update", Arrays.asList(INTENT_UPDATE, INTENT_REPLACE)
		).put(
			"upload", Arrays.asList(INTENT_CREATE)
		).put(
			"upsert", Arrays.asList(INTENT_REPLACE)
		).put(
			"validate", Arrays.asList(INTENT_VALIDATE)
		).put(
			"verify", Arrays.asList(INTENT_VALIDATE)
		).put(
			"view", Arrays.asList(INTENT_READ, INTENT_LIST)
		).put(
			"watch", Arrays.asList(INTENT_SUBSCRIBE)
		).put(
			"write", Arrays.asList(INTENT_CREATE)
		).build();

}