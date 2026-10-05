# 710: Include the Linking Verb in Messages

Write a status, error, or notification message as a complete clause with its linking verb or auxiliary in place: `Foo is not allowed`, not `Foo not allowed`; `Resource was created successfully`, not `Resource created successfully`. For a failure message, prefer `Unable to <verb>` over `Cannot <verb>`, `Failed to <verb>`, and `Error <verb>ing`. The rule applies to language property values, shell script output, workflow and other YAML scripted output, and Java log and exception messages.

This rule restores the verb only; the closing punctuation follows rule 703 for log, exception, and shell output and rule 702 for language values. When the English value of a language key changes, the key changes with it to stay the kebab case of the value.

**Rationale:** A message that drops its verb reads as a fragment and translates poorly, because the translator has to guess the tense and number the English left out. Restoring the auxiliary makes the message a sentence and matches the dominant phrasing already used across language files, log statements, and shell output. `Unable to` names the outcome the same way every time, where `Cannot`, `Failed to`, and `Error` scatter three forms across the codebase for one meaning.

A violation is a user facing or logged message that omits its linking verb, such as `Foo not allowed` or `No entries found`, or a failure message that starts with `Cannot`, `Failed to`, or `Error` followed by a verb. A title case label such as `Quota Exceeded` (rule 702) is not a violation, and neither is a phrase that has no linking verb to restore, such as `Unable to start the scan`, or an adjective noun phrase that names the problem, such as `Unknown command` or `Invalid filter`. A clause whose main verb is already in place is not a violation either: in `PageSpeed scan completed`, one of rule 703's correct examples, `completed` is the scan's own verb, whereas in `Resource created successfully` the participle needs `was` to make a clause.

**Example:** each of the following restores the verb. The shell and YAML messages take no period, because rule 703 treats them as log messages, while the language value keeps its period and its key follows the new wording.

```diff
-foo-not-allowed=Foo not allowed.
+foo-is-not-allowed=Foo is not allowed.
```

```diff
-_log "Resource ${name} created successfully."
+_log "Resource ${name} was created successfully"
```

```diff
-echo "No entries found for ${id}."
+echo "No entries were found for ${id}"
```

```diff
-throw new IllegalStateException("Foo not found for ID " + id);
+throw new IllegalStateException("Foo was not found for ID " + id);
```

```diff
-_log.warn("Cannot delete foo " + id);
+_log.warn("Unable to delete foo " + id);
```