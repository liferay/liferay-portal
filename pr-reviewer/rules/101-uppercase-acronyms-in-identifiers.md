# 101: Uppercase Acronyms in Identifiers

In an identifier you define — a class name, method name, variable name, or constant — write each acronym or initialism in all capitals rather than capitalizing only its first letter. Write `JSONObject`, `getAPIURL`, `expectedXMLNDC`, not `JsonObject`, `getApiUrl`, `expectedXmlNdc`.

**Rationale:** An acronym is a single unit. Capitalizing only its first letter (`Json`, `Api`, `Url`, `Xml`) disguises it as an ordinary word, reads as a misspelling to anyone who knows the term, and diverges from the standard it names. All capitals keeps every occurrence consistent and unmistakable.

Defer to the casing of the actual class or standard the identifier refers to. The Jackson class `JsonNode` stays `JsonNode`, never `JSONNode`, because that is the class's own name; `getJSONObject`, on the other hand, is correct because the class it returns is `JSONObject`. Likewise a variable of type `HttpServletRequest` is `httpServletRequest`, and `Id` for "identifier" stays as written in `companyId` and `userId`.

A violation is an identifier introduced in the diff that capitalizes only the first letter of an acronym, such as `Json`, `Api`, `Url`, `Xml`, `Html`, `Sql`, or `Ddm`, unless that spelling matches the class or standard the name refers to (as with `JsonNode`).

**Example:** commit `97469ec` renamed `mergeToJsonObject` to `mergeToJSONObject`; `2b917f7` renamed `getGitHubApiUrl` to `getGitHubAPIURL`; `8587510` renamed `expectedXmlNdc` to `expectedXMLNDC`. The same fix applies to a method you define and to the calls that use it.

```diff
-public String getUrl() {
+public String getURL() {
 	return _url;
 }
```

```diff
-foo.getHtmlContent();
-bar.parseXmlString();
+foo.getHTMLContent();
+bar.parseXMLString();
```