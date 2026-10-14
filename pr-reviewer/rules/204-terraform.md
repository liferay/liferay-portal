# 204: Terraform Conventions

These conventions govern every Terraform (HCL) file and depart from the Terraform ecosystem defaults in both ordering and formatting.

## Ordering

Within a Terraform block — a `resource`, `module`, `provider`, `variable`, `output`, `locals`, `terraform`, or any nested block — sort every member into one absolute alphabetical order by name. Simple arguments and nested blocks share the same order: a nested block takes its alphabetical slot among the arguments rather than being grouped before or after them. The same holds for the keys of an inline object or map literal and for a list of string literals.

Meta arguments are not special. Terraform lets `count`, `for_each`, `provider`, `lifecycle`, and `depends_on` sit anywhere in a block, and the HashiCorp style guide hoists them to the top or pushes them to the bottom. This codebase does neither: each one sorts by name like any other member, so `count` lands between `authentication_providers` and `name`, `depends_on` between an earlier `d` argument and `name`, and a `lifecycle` block between `data` and `metadata`.

Derived values follow the same absolute order. Rule 201 pulls a derived assignment out of the sorted block and places it after its dependencies, but that caveat is for imperative languages where declaration order is execution order. Terraform resolves `locals`, variable defaults, and resource references through a dependency graph, so the physical order neither reflects nor affects evaluation. A `local` computed from another `local`, or from a `var` or a resource attribute, still sorts in absolute alphabetical order, not after the values it reads. This rule overrides rule 201's derived assignment caveat for every declarative HCL file.

**Rationale:** One absolute order, with no positional carve out for nested blocks or meta arguments and no derived assignment exception, is the order with nothing to remember. A reader finds any member by name alone, every addition has exactly one correct place, and diffs stay minimal. Honoring the HashiCorp convention of hoisting `count` or trailing `lifecycle` would reintroduce the very ambiguity rule 201 removes, for no gain in a language that ignores physical order.

A violation is a Terraform block whose arguments and nested blocks are not in one absolute alphabetical order: a meta argument hoisted to the top or pushed to the bottom, a nested block grouped apart from the arguments, a derived `local` placed after the values it reads instead of in its sorted slot, or an inline object or list left unsorted.

**Example:** In `cloud/terraform/aws/eks/locals.tf` the derived `selected_azs` (computed from `local.az_count`) sorts last by name rather than next to `az_count`, and in `cloud/terraform/aws/eks/eks.tf` the `depends_on` meta argument sits in its alphabetical slot inside `kubernetes_storage_class_v1.gp3_storage_class` between `allow_volume_expansion` and `metadata` rather than being hoisted to the top of the block.

## Formatting

Indent Terraform with tabs, one tab per level, and write no space on either side of the `=` in an argument or a nested assignment: `region=var.region`, `type=string`, `tags={`. This matches the Liferay source formatter style the rest of the repository follows and departs from the Terraform ecosystem default of two space indentation with a padded `=`.

Do not run `terraform fmt`. It rewrites every file to that ecosystem default — spaces around `=` and space based indentation — and so fights the committed style across the whole tree. Format by hand to the convention above, and leave the existing formatting in place.

**Rationale:** A single whitespace convention across Terraform, shell, and the rest of the codebase means a reader never reorients between files, and a tight `=` keeps an argument list dense and scannable. Letting `terraform fmt` reformat would churn every line it touches, bury the real change in a diff, and split the tree between two conventions.

A violation is a Terraform file indented with spaces, an argument with a space before or after its `=`, or a diff that shows `terraform fmt` reformatting unrelated lines.