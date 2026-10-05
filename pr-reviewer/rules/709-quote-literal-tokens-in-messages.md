# 709: Quote Literal Tokens in Messages

Wrap a literal token inside a log or exception message in escaped double quotes: an identifier, a header name, a content type, a property key, a JSON member name, or any other value that is data rather than prose. Write `"Missing \"paths\" object"`, using `\"` and never single quotes.

**Rationale:** Quoting separates the literal from the surrounding sentence, so a reader can tell at a glance which words come from the data and which from the prose. `Missing header X-Custom-Header for request` makes the reader parse the token out of the sentence; the quoted form does not. Escaped double quotes are the codebase convention, so a single quoted token is a second form for the same thing.

A violation is a log or exception message that embeds a literal token unquoted, or wraps it in single quotes. A value concatenated into the message from a variable is not covered; only tokens written into the message text itself are.

**Example:** a message naming a content type, a header, or a JSON member spells the token inside escaped double quotes.

```diff
 throw new IllegalArgumentException(
-	"Request body has no application/json content");
+	"Request body has no \"application/json\" content");
```

```diff
-_log.warn("Missing header X-Custom-Header for request");
+_log.warn("Missing header \"X-Custom-Header\" for request");
```

```diff
-throw new IllegalArgumentException("Missing 'paths' object");
+throw new IllegalArgumentException("Missing \"paths\" object");
```