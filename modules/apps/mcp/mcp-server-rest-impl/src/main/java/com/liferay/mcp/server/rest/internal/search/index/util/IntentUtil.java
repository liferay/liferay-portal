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

	public static final String INTENT_CREATE = "create";

	public static final String INTENT_LIST = "list";

	public static final String INTENT_READ = "read";

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

	private static final String _INTENT_COPY = "copy";

	private static final String _INTENT_DELETE = "delete";

	private static final String _INTENT_DEPLOY = "deploy";

	private static final String _INTENT_MOVE = "move";

	private static final String _INTENT_PUBLISH = "publish";

	private static final String _INTENT_REPLACE = "replace";

	private static final String _INTENT_RESTORE = "restore";

	private static final String _INTENT_SUBSCRIBE = "subscribe";

	private static final String _INTENT_TRANSLATE = "translate";

	private static final String _INTENT_UNDEPLOY = "undeploy";

	private static final String _INTENT_UNPUBLISH = "unpublish";

	private static final String _INTENT_UNSUBSCRIBE = "unsubscribe";

	private static final String _INTENT_UPDATE = "update";

	private static final String _INTENT_VALIDATE = "validate";

	private static final Map<String, String> _methodIntents =
		HashMapBuilder.put(
			"delete", _INTENT_DELETE
		).put(
			"patch", _INTENT_UPDATE
		).put(
			"post", INTENT_CREATE
		).put(
			"put", _INTENT_REPLACE
		).build();
	private static final Map<String, String> _toolActionIntents =
		HashMapBuilder.put(
			"Copy", _INTENT_COPY
		).put(
			"CopyReplace", _INTENT_COPY
		).put(
			"Deploy", _INTENT_DEPLOY
		).put(
			"Expire", _INTENT_UNPUBLISH
		).put(
			"Move", _INTENT_MOVE
		).put(
			"MoveReplace", _INTENT_MOVE
		).put(
			"Publish", _INTENT_PUBLISH
		).put(
			"Restore", _INTENT_RESTORE
		).put(
			"Subscribe", _INTENT_SUBSCRIBE
		).put(
			"Translation", _INTENT_TRANSLATE
		).put(
			"TranslationLanguage", _INTENT_TRANSLATE
		).put(
			"Undeploy", _INTENT_UNDEPLOY
		).put(
			"Unsubscribe", _INTENT_UNSUBSCRIBE
		).put(
			"Validate", _INTENT_VALIDATE
		).build();
	private static final Map<String, List<String>> _verbIntents =
		HashMapBuilder.<String, List<String>>put(
			"activate",
			Arrays.asList(_INTENT_PUBLISH, _INTENT_DEPLOY, _INTENT_UPDATE)
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
			"change", Arrays.asList(_INTENT_UPDATE, _INTENT_REPLACE)
		).put(
			"check", Arrays.asList(_INTENT_VALIDATE, INTENT_LIST, INTENT_READ)
		).put(
			"clone", Arrays.asList(_INTENT_COPY)
		).put(
			"copy", Arrays.asList(_INTENT_COPY)
		).put(
			"create", Arrays.asList(INTENT_CREATE)
		).put(
			"deactivate", Arrays.asList(_INTENT_UNDEPLOY, _INTENT_UPDATE)
		).put(
			"delete", Arrays.asList(_INTENT_DELETE)
		).put(
			"deploy", Arrays.asList(_INTENT_DEPLOY)
		).put(
			"destroy", Arrays.asList(_INTENT_DELETE)
		).put(
			"detach", Arrays.asList(_INTENT_DELETE)
		).put(
			"disassociate", Arrays.asList(_INTENT_DELETE)
		).put(
			"duplicate", Arrays.asList(_INTENT_COPY)
		).put(
			"edit", Arrays.asList(_INTENT_UPDATE, _INTENT_REPLACE)
		).put(
			"expire", Arrays.asList(_INTENT_UNPUBLISH)
		).put(
			"fetch", Arrays.asList(INTENT_READ, INTENT_LIST)
		).put(
			"find", Arrays.asList(INTENT_LIST, INTENT_READ)
		).put(
			"follow", Arrays.asList(_INTENT_SUBSCRIBE)
		).put(
			"get", Arrays.asList(INTENT_READ, INTENT_LIST)
		).put(
			"get all", Arrays.asList(INTENT_LIST)
		).put(
			"get rid of", Arrays.asList(_INTENT_DELETE)
		).put(
			"link", Arrays.asList(INTENT_CREATE)
		).put(
			"list", Arrays.asList(INTENT_LIST, INTENT_READ)
		).put(
			"localize", Arrays.asList(_INTENT_TRANSLATE)
		).put(
			"look up", Arrays.asList(INTENT_READ, INTENT_LIST)
		).put(
			"make", Arrays.asList(INTENT_CREATE)
		).put(
			"make live", Arrays.asList(_INTENT_PUBLISH)
		).put(
			"modify", Arrays.asList(_INTENT_UPDATE, _INTENT_REPLACE)
		).put(
			"move", Arrays.asList(_INTENT_MOVE)
		).put(
			"open", Arrays.asList(INTENT_READ, INTENT_LIST)
		).put(
			"overwrite", Arrays.asList(_INTENT_REPLACE)
		).put(
			"patch", Arrays.asList(_INTENT_UPDATE)
		).put(
			"place", Arrays.asList(INTENT_CREATE)
		).put(
			"post", Arrays.asList(INTENT_CREATE)
		).put(
			"publish", Arrays.asList(_INTENT_PUBLISH)
		).put(
			"put", Arrays.asList(_INTENT_REPLACE)
		).put(
			"read", Arrays.asList(INTENT_READ, INTENT_LIST)
		).put(
			"recover", Arrays.asList(_INTENT_RESTORE)
		).put(
			"relate", Arrays.asList(INTENT_CREATE)
		).put(
			"relocate", Arrays.asList(_INTENT_MOVE)
		).put(
			"remove", Arrays.asList(_INTENT_DELETE)
		).put(
			"rename", Arrays.asList(_INTENT_UPDATE, _INTENT_REPLACE)
		).put(
			"replace", Arrays.asList(_INTENT_REPLACE)
		).put(
			"restore", Arrays.asList(_INTENT_RESTORE)
		).put(
			"retire", Arrays.asList(_INTENT_UNPUBLISH)
		).put(
			"retrieve", Arrays.asList(INTENT_READ, INTENT_LIST)
		).put(
			"see", Arrays.asList(INTENT_LIST, INTENT_READ)
		).put(
			"set", Arrays.asList(_INTENT_REPLACE, _INTENT_UPDATE)
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
			"subscribe", Arrays.asList(_INTENT_SUBSCRIBE)
		).put(
			"transfer", Arrays.asList(_INTENT_MOVE)
		).put(
			"translate", Arrays.asList(_INTENT_TRANSLATE)
		).put(
			"unassign", Arrays.asList(_INTENT_DELETE)
		).put(
			"undelete", Arrays.asList(_INTENT_RESTORE)
		).put(
			"undeploy", Arrays.asList(_INTENT_UNDEPLOY)
		).put(
			"unfollow", Arrays.asList(_INTENT_UNSUBSCRIBE)
		).put(
			"unlink", Arrays.asList(_INTENT_DELETE)
		).put(
			"unpublish", Arrays.asList(_INTENT_UNPUBLISH)
		).put(
			"unsubscribe", Arrays.asList(_INTENT_UNSUBSCRIBE)
		).put(
			"unwatch", Arrays.asList(_INTENT_UNSUBSCRIBE)
		).put(
			"update", Arrays.asList(_INTENT_UPDATE, _INTENT_REPLACE)
		).put(
			"upload", Arrays.asList(INTENT_CREATE)
		).put(
			"upsert", Arrays.asList(_INTENT_REPLACE)
		).put(
			"validate", Arrays.asList(_INTENT_VALIDATE)
		).put(
			"verify", Arrays.asList(_INTENT_VALIDATE)
		).put(
			"view", Arrays.asList(INTENT_READ, INTENT_LIST)
		).put(
			"watch", Arrays.asList(_INTENT_SUBSCRIBE)
		).put(
			"write", Arrays.asList(INTENT_CREATE)
		).build();

}