# 702: Title Case Labels, or Write a Complete Sentence

Write a short user facing label as a title in APA title case — the standard at https://capitalizemytitle.com/style/APA. When the text is a description or help text rather than a short label, write it as a complete sentence ending in a period — or ending in a colon if the sentence introduces a list or other text that follows. Pick one or the other; do not leave a label in arbitrary or fragmentary casing.

APA title case capitalizes the first word, every major word (noun, verb, adjective, adverb, pronoun), and every word of four or more letters. It lowercases minor words of three or fewer letters — articles (`a`, `an`, `the`), short coordinating conjunctions (`and`, `but`, `or`, `nor`, `for`, `so`, `yet`), and short prepositions (`at`, `by`, `in`, `of`, `on`, `to`, `up`, `via`) — unless the word is first. A four letter preposition is therefore capitalized: write `With`, not `with`.

In `Language.properties`, the same split decides a value's casing. A short UI label, title, button, menu item, or progress or status phrase such as `Searching for Foos...` is a label and takes title case. A message, description, confirmation, or tooltip with a subject and a verb is a sentence and takes sentence case, capitalizing only the first word and any proper nouns. Sibling entries added in one change must agree on which form they take. The key is unaffected either way: it stays the kebab case of the English value.

**Rationale:** Labels are read as headings, and inconsistent capitalization (`On The Category`, `at risk accounts`) looks unfinished and shifts from screen to screen. Pinning every label to one well defined standard removes the per label argument and makes them uniform. Text that is really a sentence should look like one — a full clause with a period — not a half capitalized fragment. Liferay's UI copy is consistent about this, so a miscased language value stands out in the interface; the rule is not to capitalize everything but to separate label from sentence, so both directions matter.

A violation is a user facing label that is neither correct APA title case nor a complete sentence: a fragment with arbitrary capitalization, a mid title minor word wrongly capitalized (`On The Category`), a word of four or more letters wrongly lowercased (`with`, `from`), or a description written without a verb or any closing punctuation. In a language value, a violation is also a label written in sentence case (`Click here`), a sentence forced into title case, or sibling entries added in one change that disagree on casing. A sentence ending in a colon to introduce a list or other text is not a violation, and neither is a complete sentence in sentence case such as `Great! I have added {0} foos to your bar.`; do not force title case onto it.

**Example:** commit `1cd3345` ("Use 'With' and not 'with' for titles") capitalizes the four letter preposition. Many commits point reviewers to the standard (`cd559f1`, `3b68285` cite https://capitalizemytitle.com/style/APA), and others (`352decf`, `06384ff`) ask for "a title or a sentence", with a period when sentence formatting is used. On https://github.com/brianchandotcom/liferay-portal/pull/179011 a reviewer flagged sentence cased labels ("Keys are wrong.. not just this one"), corrected in https://github.com/brianchandotcom/liferay-portal/pull/179026 (`LPD-95940`).

```diff
-click-here=Click here
+click-here=Click Here
```

```diff
-searching-for-foos=Searching for foos...
+searching-for-foos=Searching for Foos...
```