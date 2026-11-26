# Index Action Label

A new indexer or index reindexer needs a matching `model.resource.<class name>` language key. Search Admin's **Index Actions** tab labels every reindex action with that key and shows the raw key when the key is missing, which fails the Playwright test `portal-search-admin-web/main/indexActions.spec.ts`. Nothing else here catches it because that test lives in a module that the pull request never touches.

## Match

`(^|/)[^/]+(Indexer|ModelSearchConfigurator|Reindexer)\.java$ &! (^|/)src/(test|testIntegration)/`

## Command

This check is static and needs no build. List the added lines of the selected files:

```bash
bash \
	"${SKILL_DIR}/select_paths.sh" \
	"${MERGE_BASE}" \
	"${VALIDATION_FILE}" \
	| xargs \
		git \
		diff \
		"${MERGE_BASE}...HEAD" \
		--
```

Work out the keys that each added registration needs:

- **Index reindexer.** A class registered with `@Component(service = IndexReindexer.class)` needs `model.resource.<fully qualified name of the class itself>`, such as `model.resource.com.liferay.portal.search.tuning.rankings.web.internal.index.RankingIndexReindexer`.

- **Indexer.** A class that extends `BaseIndexer` or is registered as an `Indexer` or `ModelSearchConfigurator` needs `model.resource.<getClassName()>`, the name of the model class that it indexes. When that class name contains `.model.`, it also needs `model.resource.<the part before .model.>`, which labels the group the indexer is listed under.

Skip a class name containing `#`, such as an object definition's, whose label comes from the definition rather than a key.

Report NOT APPLICABLE when no added line registers an indexer or reindexer, as when the branch only edits an existing one or changes a class that merely ends in `Indexer`, such as `PortalCacheIndexer`.

Confirm that each key exists in the global `modules/apps/portal-language/portal-language-lang/src/main/resources/content/Language.properties`, and report any missing key as a FAIL. Report a value as a FAIL when it holds anything other than ASCII letters, digits, underscores, and spaces, because the label in the test must match `^[\w\s]+`, whose `\w` matches no accented letter. Follow the existing reindexer keys and name what is indexed rather than the class, such as `RankingIndexReindexer=Ranking` rather than `Ranking Index Reindexer`.

## Time Estimate

~10-30 sec (static; no build).