# 206: Keep a Conditional Assignment in Its Alphabetical Slot

When a constructor or a setter assigns a run of fields from a builder — or from parameters in a body that rule 202's constructor clause does not govern, such as a private constructor that takes a builder — alphabetical order by field name governs the whole run, including a field assigned by a multi statement conditional block. The block takes its own slot in that order and splits the simple assignments into a group before it and a group after it. When the assignments come straight from the constructor's own parameters, rule 202's constructor clause wins instead: those assignments follow the parameter order, with derived assignments after them, and this rule does not apply.

This extends rule 201 to an assignment that spans several statements. That rule sorts assignments already grouped into one consecutive block and pulls a derived assignment out next to its first use; a conditional assignment is neither, so it drifts to wherever it was typed. The rule yields to a real dependency: when the conditional block reads a field assigned earlier in the same body, that order wins over the alphabet, exactly as in rule 201.

**Rationale:** A reader looking for one field in a twenty line constructor scans down the field names, so anything that leaves the alphabet costs a full read of the method. Spending five lines on a field instead of one does not earn it an exemption, and the end of the constructor carries no information about which field the block assigns. Keeping the block in its slot means every assignment, long or short, is where the alphabet says it is.

A violation is a conditional assignment parked at the end of the body, or hoisted to the top, when the field it assigns sorts into the middle of the simple builder sourced assignments. Do not flag a block that must follow a field it reads, such as `_account` computed from `_companyId` after `_companyId = builder._companyId;`, and do not flag a constructor whose assignments come from its own parameters and follow the parameter order per rule 202.

**Example:** PR 50028 (`LPD-101112`) in `brianchandotcom/liferay-portal-ee`, where Brian Chan commented "I went ahead and did the SF" and, in commit `93ad072`, moved the `_subagents` conditional block of `AgentContext` into its slot between `_sseEventSinkKey` and `_userId`.

```diff
 		_alpha = builder._alpha;
 		_beta = builder._beta;
-		_delta = builder._delta;
-		_epsilon = builder._epsilon;

 		if (builder._gammaFunction != null) {
 			_gamma = builder._gammaFunction.apply(this);
 		}
 		else {
 			_gamma = new Object[0];
 		}
+
+		_delta = builder._delta;
+		_epsilon = builder._epsilon;
 	}
```