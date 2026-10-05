# 711: Use One Space After a Period

Put a single space after a period, or any other sentence ending punctuation, in prose: comments, JSP text, language property values, Markdown, and log and exception messages. Do not put two.

**Rationale:** Two spaces after a period is a legacy typewriter convention. Modern Liferay prose uses one, and a stray double space is invisible in the rendered UI yet shows up as an inconsistency in the source and in every diff that touches the line.

A violation is a sentence ending period followed by two or more spaces in prose. Whitespace that aligns code or pads a string literal for output formatting is not a violation.

**Example:** a comment with two sentences separates them with one space.

```diff
-// Compute the score.  Cache it for next time.
+// Compute the score. Cache it for next time.
```