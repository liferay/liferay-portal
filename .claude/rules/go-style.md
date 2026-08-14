---

paths:
  - "cloud/operator/**/*.go"

---

# Go Style

These conventions apply to every Go file under `cloud/operator`, which holds the only Go module in the repository. They depart from the Go ecosystem defaults in naming and ordering, and they carry the same philosophy as the canonical rules in `pr-reviewer/rules` — whole word names, alphabetical ordering, blank line delimited groups, complete sentence messages — expressed in the mechanics Go needs. A Go file outside that module, such as a Gradle plugin test fixture, is not governed by them. For prose and general style not specific to Go, follow `pr-reviewer/rules`.

`gofmt` owns whitespace, alignment, and import sorting; the rules below cover only what it does not enforce. When a rule here and `gofmt` disagree, `gofmt` wins.

## Chained Calls

Break a multicall chain so that each call in the chain starts a line, its arguments are indented on their own lines, and the closing parenthesis carries the next call. The receiver's opening call keeps its argument on a separate line too.

**Rationale:** This is the chained call form the Liferay Java code uses, so a builder reads the same in either language. Each link in the chain starts at the same column, which makes the sequence of operations scannable and keeps a diff to the one call that changed.

```go
return controllerruntime.NewControllerManagedBy(
	manager,
).For(
	&appsv1.StatefulSet{},
	builder.WithPredicates(statefulSetPredicate),
).Named(
	"liferaystatefulset",
).Owns(
	&corev1.PersistentVolumeClaim{},
).Complete(
	liferayStatefulSetReconciler,
)
```

A short chain that carries no arguments of its own stays inline — `logger.V(1).Info(...)`, `liferayEnvironmentReconciler.Status().Update(context, liferayEnvironment)`. The broken form is for builders, where each call takes arguments and the chain is the structure of the statement.

Rule 402 allows a chain only on a builder or on a fluent type from the list that `ChainingCheck` maintains, and Go has no equivalent list. `Status()` and `V(1)` are the exception: each returns a narrowed view of the same object rather than a new value to inspect, and the controller runtime and logr APIs are built to be called that way. Everything else follows rule 402 — assign the intermediate result to a named local and call the next method on it.

## Declaration Order

A file declares its members in one fixed sequence:

1. `import` block.

1. `const` declarations, sorted alphabetically. A single constant needs no parentheses. A block whose values come from `iota` keeps its semantic order, since the position of a name determines its value.

1. Functions — exported names first, then unexported, alphabetical within each group. Methods and plain functions share one sequence: the receiver is not a grouping key, so a plain function takes its alphabetical slot among the methods rather than a block of its own. `init` and `main` sort alphabetically among the unexported functions like any other name.

1. `type` declarations, sorted alphabetically.

1. Package level `var` declarations.

Every fixed set of named members is alphabetical: struct field declarations, composite literal fields, string keyed map literals, interface methods, and the key value pairs passed to a structured logger.

Function parameters are alphabetical by name, which overrides the Go idiom of placing `context.Context` first — the context parameter sorts into its alphabetical slot like any other, as does `t *testing.T`. A variadic parameter is forced last, since the language requires it. A signature the compiler fixes — a method that satisfies an interface, or a callback a package defines — keeps the order that signature imposes, since no other order compiles. Rename its parameters to the full word form and leave the order alone.

**Rationale:** The reader finds the entry points at the top and the data definitions at the bottom, mirroring how Liferay places fields beneath methods in a Java class. One absolute order means a member's position is predictable and every addition has exactly one correct home.

The struct definition sits below the methods that operate on it:

```diff
-type HTTPClient struct {
-	BaseURL string
-	Client  *http.Client
-}
-
 func (httpClient *HTTPClient) Activate(activationRequest ActivationRequest) error {
 }

 func (httpClient *HTTPClient) post(url string) (*http.Response, error) {
 }
+
+type HTTPClient struct {
+	BaseURL string
+	Client  *http.Client
+}
```

Exported functions sort ahead of unexported ones:

```diff
-func decodeSegment(segment string) []byte {
-}
-
 func Activate(activationRequest ActivationRequest) error {
 }

+func decodeSegment(segment string) []byte {
+}
```

Map literal keys and logger key value pairs are alphabetical:

```diff
 claims := map[string]any{
-	"environmentName": environmentName,
 	"activationCode":  activationCode,
+	"environmentName": environmentName,
 	"publicKey":       publicKey,
 }
```

```diff
 logger.Info(
 	"Enforced licensed replica ceiling",
 	"desiredReplicas", desiredReplicas,
 	"effectiveReplicas", effectiveReplicas,
-	"workload", statefulSet.Name,
 	"maxClusterNodes", maxClusterNodes,
+	"workload", statefulSet.Name,
 )
```

Parameters sort alphabetically, including `context.Context` and `t *testing.T`:

```diff
 func Activate(
-	context context.Context,
 	activationRequest ActivationRequest,
+	context context.Context,
 	privateKey *rsa.PrivateKey,
 ) error {
```

### Embedded Fields Lead the Struct

An embedded field goes above the named fields, separated from them by a blank line. Multiple embedded fields sort alphabetically among themselves.

```go
type LiferayEnvironmentReconciler struct {
	client.Client

	GracePeriod       time.Duration
	HeartbeatInterval time.Duration
	Provisioning      provisioning.Client
	Recorder          record.EventRecorder
	RetryInitialDelay time.Duration
	RetryMaxDelay     time.Duration
}
```

### A Derived `var` Follows What It Reads

Alphabetical order yields to dependency order in a `var` block, exactly as rule 201 places a derived assignment after the values it depends on. `AddToScheme` reads `SchemeBuilder`, so it sits below it rather than above:

```go
var (
	SchemeBuilder = &scheme.Builder{
		GroupVersion: schema.GroupVersion{
			Group:   "licensing.liferay.com",
			Version: "v1alpha1",
		},
	}

	AddToScheme = SchemeBuilder.AddToScheme
)
```

## Error Values

Name the error value with the full word `error`, not `err`. When two errors coexist in one scope — typically because one is inspected again after a later statement — prefix each with what produced it (`configError`, `getError`) rather than numbering them.

When the call also returns a value used after the check, assign on its own line, leave a blank line, then guard with `if error != nil`. Fold the call into the `if` initializer only when the value is discarded and just the error matters, which also scopes the error to the branch.

**Rationale:** Liferay spells identifiers as whole words, so the error takes the full word — mirroring the Java `catch (Exception exception)` convention. The blank line before the check marks the "compute, then verify" step the way rule 201 separates assignment groups.

```diff
-result, err := doSomething(input)
+result, error := doSomething(input)

-if err != nil {
-	return err
+if error != nil {
+	return error
 }
```

The `Get` error is inspected twice — once for `IsNotFound`, once as a plain failure — so it takes a descriptive name:

```go
getError := liferayEnvironmentReconciler.Get(
	context, types.NamespacedName{
		Name:      identityName,
		Namespace: liferayEnvironment.Namespace,
	}, secret)

if getError == nil {
	return parsePrivateKey(secret.Data["private.pem"])
}

if !errors.IsNotFound(getError) {
	return nil, getError
}
```

The result is used below, so the assignment stands on its own line:

```diff
-if token, error := signJWT(claims, privateKey); error != nil {
-	return "", error
-}
+token, error := signJWT(claims, privateKey)
+
+if error != nil {
+	return "", error
+}
```

Only the error is consumed, so the inline initializer is correct:

```go
if error := reconciler.Create(context, secret); error != nil {
	return error
}
```

## Formatting

`gofmt` is the automatic formatter for Go, since the portal source formatter does not process `*.go`. Run it over the Go module — the directory that holds `go.mod` — after every edit:

```bash
cd <go-module-root> && gofmt -w .
```

Do not hand format against it. The `format-source` skill owns the full workflow; its `Go Code` section covers the `gofmt` passes that bracket these rules.

## Generated Files

Leave generated Go alone, just as the source formatter skips `@generated` Java. A Go file is generated when its name contains `zz_generated` or it carries the canonical marker line:

```go
// Code generated ... DO NOT EDIT.
```

The `//go:generate` directives that produce them live in `generate.go` at the module root. Generated output may also be untracked — `zz_generated.deepcopy.go` is listed in `cloud/.gitignore` and is rebuilt by `go generate`, so a fresh checkout does not compile or vet until it is regenerated.

**Rationale:** The next `go generate` overwrites the file, so a manual edit is lost and only pollutes the diff.

### Regenerate After Editing the API Types

Editing a type or a `+kubebuilder:` marker under `cloud/operator/resources/api` changes the CustomResourceDefinition that controller-gen derives from it. Regenerate and commit before continuing with any other work, the same discipline `.claude/rules/service-builder.md` and `.claude/rules/rest-builder.md` apply to their generators:

1. Commit the hand written change to the types.

1. Run the generator from `cloud/operator`:

	```bash
	./go_build.sh generate
	```

1. Commit what it rewrites — `cloud/helm/dxp-operator/crds/licensing.liferay.com_liferayenvironments.yaml` — on its own, titled `<TICKET> go generate`.

1. Continue with the work.

Running `./go_build.sh generate` on a clean tree must leave the tree clean. A dirty tree afterward means the committed CRD no longer matches the types that produce it, whether because a regeneration was skipped, because the controller-gen version moved, or because the source formatter that `go_build.sh` downloads disagrees with the one the repository resolves. The `Check generated files are up to date` step in `.github/workflows/ci-reusable-test-cloud-operator.yaml` asserts this.

**Rationale:** The CRD is the API contract the cluster validates against. A stale one silently rejects or drops fields the types already declare, and because the CRD lives outside `cloud/operator`, a drift confined to it does not trigger the operator's CI workflow.

## Imports

Standard library imports form the first group, unaliased. Every other import goes in a single second group, each with an explicit alias, sorted by import path — including intramodule imports, and including the case where the alias matches the package's own name.

**Rationale:** The identifier a file uses is declared at the import rather than inferred from the path's last segment, which for Kubernetes packages is often neither obvious nor unique. One group for everything nonstandard means there is no judgment call about which subgroup a new dependency belongs to.

```diff
 import (
 	"context"
 	"fmt"

-	"k8s.io/apimachinery/pkg/api/errors"
-	"sigs.k8s.io/controller-runtime/pkg/builder"
+	errors "k8s.io/apimachinery/pkg/api/errors"
+	builder "sigs.k8s.io/controller-runtime/pkg/builder"
 )
```

## Messages

Three kinds of message, three forms.

**Structured logger messages** are capitalized and carry no terminal period. Prefer `Unable to <verb>` for failures. The message is a fixed label — every variable belongs in the key value pairs that follow it, not interpolated into the message.

**User facing messages** — a `metav1.Condition` message, a recorded event, an admission response — are complete sentences with terminal punctuation, since a person reads them out of `kubectl describe`. Report a failure in the active voice with `Unable to`, and state a failed existence or precondition check in the present tense: `does not exist` rather than `was not found`.

**Error values** built with `fmt.Errorf` or `errors.New` are lowercase with no terminal period, so they compose cleanly when a caller wraps them with `%w`. Where the package has a natural subject, lead with it as a `subject: detail` prefix.

**Rationale:** Logger messages are grep keys and read best as stable labels, which is why the variables move to the key value pairs; error values are fragments that get concatenated, so they start lowercase and end without punctuation.

A user facing message is product prose that a person reads, not a log or an exception message, so rule 707 governs it and rule 703 does not. Rule 703 rejects the lone sentence that ends with a period, and that is exactly the form a condition message wants. Logger messages and error values are what rule 703 does govern, and both forms above already satisfy it by carrying no terminal period at all.

```diff
-setupLog.Error(error, "couldn't start manager.")
+setupLog.Error(error, "Unable to start manager")
```

```diff
-logger.Info(fmt.Sprintf("Enqueuing StatefulSet %s", workloadName))
+logger.Info("Enqueuing StatefulSet referenced by LiferayEnvironment", "statefulSet", workloadName)
```

```diff
 metav1.Condition{
-	Message: "workload statefulset not found",
+	Message: fmt.Sprintf(
+		"Workload StatefulSet %q does not exist.",
+		liferayEnvironment.Spec.WorkloadRef.Name,
+	),
 	Reason: "WorkloadNotFound",
 }
```

```diff
-return fmt.Errorf("Entitlements Decode Failed: %w", error)
+return fmt.Errorf("entitlements: decode response: %w", error)
```

## Naming

Name receivers, parameters, and locals with the full word that echoes the type, never the Go idiomatic abbreviation. A receiver takes the lowerCamel form of its type (`httpClient`, `liferayEnvironmentReconciler`), and a parameter takes the name its type suggests (`context context.Context`, `privateKey *rsa.PrivateKey`, `activationRequest ActivationRequest`). There is no `err`, `ctx`, `req`, or `cfg` anywhere in the tree.

**Rationale:** The unabbreviated name documents the value at every use and never forces the reader to map a letter back to a type. A receiver then reads the same as every local and parameter around it.

```diff
-func (r *LiferayEnvironmentReconciler) Reconcile(
+func (liferayEnvironmentReconciler *LiferayEnvironmentReconciler) Reconcile(
```

```diff
-func sign(ctx context.Context, pk *rsa.PrivateKey, req Request) error {
+func sign(context context.Context, privateKey *rsa.PrivateKey, request Request) error {
```

Naming after the type wins even when the name shadows the type or an imported package — `var licenseSet licenseSet`, `func encodeSegment(bytes []byte)`. Let the shadow stand rather than reaching for an abbreviation.

Two names keep their conventional short form, because the language or the standard library fixes them:

- `ok`, the second result of a comma ok type assertion, map index, or channel receive.

- `t`, the `*testing.T` parameter.

## Statement Grouping

Separate statements with a blank line. Assignments that write to the same destination stay together as one group, and a group is separated from what follows.

**Rationale:** This is rule 201's grouping applied to Go. Each paragraph is one step, so the reader sees the shape of a function before reading any line of it, and a diff that adds a step adds a paragraph rather than editing one.

```go
liferayEnvironment.Status.License.Checksum = licenseChecksum(entitlements.LicenseXML)
liferayEnvironment.Status.License.LastVerified = &now
liferayEnvironment.Status.License.MaxClusterNodes = entitlements.MaxClusterNodes

expirationDate, error := license.ExpirationDate(entitlements.LicenseXML)

if error != nil {
	return controllerruntime.Result{}, error
}
```

## Tests

Tests live in the same package as the code under test, not a `_test` package, so an unexported function can be tested directly.

Name a test `Test<Subject><Behavior>` — `TestExpirationDateReturnsVirtualClusterDate`, `TestReconcileBacksOffWhenActivationRejected` — after the function it exercises, per rule 603. Declaration order is the ordinary one: exported names first and alphabetical, so `Test*` functions sort among any exported stub methods; then unexported helpers alphabetically; then the stub types at the bottom.

Write a multicase test as a table — a `map[string]struct{...}` keyed by a lowercase description of the case, with the struct fields and the map keys both alphabetical — and run each case through `t.Run`.

**Rationale:** The map key is the subtest name, so a failure reports the case in words. Ordinary declaration order applies to test files because a test file is a Go file; nothing about `_test.go` changes where a reader looks for things.

```go
testCases := map[string]struct {
	assertContains    string
	assertNotContains string
	payload           string
}{
	"activationCode is redacted": {
		assertContains:    `"activationCode":"[REDACTED]"`,
		assertNotContains: "one-time-secret",
		payload:           `{"activationCode": "one-time-secret"}`,
	},
}

for name, testCase := range testCases {
	t.Run(name, func(t *testing.T) {
		...
	})
}
```

A failure message names the thing it checked and states what was wanted, in either the `<call> = %q, want %q` form or the `Expected %q, got %q` form.

## Variable Declaration

Declare a composite value that starts at its zero value and gets populated later as `var x T`, not `x := T{}`. This covers `Decode` and `Unmarshal` targets and any struct or map whose contents are filled in on the lines that follow.

**Rationale:** `var x T` says "start from the zero value; something else fills it in," which is exactly what `json.NewDecoder(...).Decode(&x)` does. `x := T{}` reads as "construct this specific empty literal as my value," which misleads a reader when the next line overwrites it. Both forms compile identically, so this is a matter of what the declaration announces.

```diff
-entitlementsResponse := EntitlementsResponse{}
+var entitlementsResponse EntitlementsResponse

 if error := json.NewDecoder(response.Body).Decode(&entitlementsResponse); error != nil {
 	return nil, fmt.Errorf("entitlements: decode response: %w", error)
 }
```

Four forms are not violations:

- A composite literal that actually sets fields — `entitlements := &Entitlements{AddOns: ...}` — is a real construction, not a zero value.

- A pointer to an API object handed to a client or decoder — `secret := &corev1.Secret{}` before a `Get`, `scale := &autoscalingv1.Scale{}` before a `Decode`, `liferayEnvironmentList := &licensingv1alpha1.LiferayEnvironmentList{}` before a `List`. The pointer literal is the idiomatic controller-runtime form, and `var secret corev1.Secret` would force `&secret` at every call site for no gain.

- A scalar seeded with its zero value keeps the short form and the explicit literal — `redacted := false`, `storageClassName := ""`. There is no `T{}` to mislead anyone, and the literal shows the starting value at a glance.

- An empty slice or map literal written to force a non-`nil` value — `addOns = []provisioning.AddOn{}` — is deliberate, since `nil` and `[]T{}` marshal to `null` and `[]`.