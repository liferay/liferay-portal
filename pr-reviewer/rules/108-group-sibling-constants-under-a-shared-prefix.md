# 108: Group Sibling Constants Under a Shared Prefix

This rule governs `static final` constant names in uppercase, the `_SCREAMING_SNAKE_CASE` form, not lowercase fields or variables such as `_xyz`. When two or more such constants name sibling values of one kind, lead each name with the shared category and distinguish it by a suffix, ordering every name general to specific — the category first, the detail last — so the set sorts together and reads as a family. Distinct but related members share the category prefix: `_ANIMAL_HORSE` and `_ANIMAL_ZEBRA`, not `_HORSE_ANIMAL` beside `_ANIMAL_ZEBRA`, where one name leads with the detail and the next with the category, so the pair scatters and never reads as a set. When the members are interchangeable instances of one thing, number them off the shared stem instead: `_HORSE_1`, `_HORSE_2`, `_HORSE_3`. The category leads even when every sibling already ends with it: `_FOO_NAME_ALPHA` and `_FOO_NAME_BETA`, not `_ALPHA_FOO_NAME` and `_BETA_FOO_NAME`, which sort under their detail words and let unrelated constants land between them.

**Rationale:** A shared lead clusters related constants wherever names are sorted and signals at a glance that they belong together, so the reader meets the whole family in one place. Names that bury the common word — one leading with the category, the next with a detail — split a single concept across the file and hide that the members are siblings. This is the naming counterpart to rule 106: there each name mirrors its type one to one; here each name leads with the kind it shares with its siblings.

A violation is two or more uppercase `static final` constants of the same kind whose names do not share a leading prefix, so they neither sort together nor read as a set, where reordering each name general to specific — or numbering interchangeable members off a shared stem — would group them. Lowercase fields and variables are out of scope.

**Example:** review of PR 176933 flagged `_CONSENT_MAPPING_SCRIPT` beside `_SCRIPT_TAG`; both hold a script tag string for the same test, so they should share the `_SCRIPT_` lead (`_SCRIPT_CONSENT_MAPPING`, `_SCRIPT_TAG`), or be numbered `_SCRIPT_1` and `_SCRIPT_2` if interchangeable, rather than leading with two different words. Two bundle name constants that both trailed with the category were turned around to lead with it.

```diff
-private static final String _ALPHA_FOO_NAME = "...";
-private static final String _BETA_FOO_NAME = "...";
+private static final String _FOO_NAME_ALPHA = "...";
+private static final String _FOO_NAME_BETA = "...";
```