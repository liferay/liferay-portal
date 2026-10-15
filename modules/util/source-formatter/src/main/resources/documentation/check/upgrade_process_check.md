## UpgradeProcessCheck

If there is a `if (hasColumn(...)) {...}` or `if (hasColumnType(...)) {...}`
(or negative conditionals) that only contains calls to the methods `alterColumnName`, `alterColumnType`,
`alterTableAddColumn` and/or `alterTableDropColumn`, the if-statement must be
removed, leaving the `alter*` methods.

### Examples

Incorrect:

```java
if (hasColumn("MFAFIDO2CredentialEntry", "credentialKey")) {
	alterColumnType(
		"MFAFIDO2CredentialEntry", "credentialKey", "TEXT null");
}
```

Correct:

```java
alterColumnType(
	"MFAFIDO2CredentialEntry", "credentialKey", "TEXT null");
```

---

If an upgrade process class only contains calls to the methods
`alterColumnName`, `alterColumnType`, `alterTableAddColumn` and/or
`alterTableDropColumn`, no need to create the upgrade process class and replace
it by inline calls to the `UpgradeProcessFactory` class in the registrator
class.

### Examples

Incorrect:

```java
public class AccountEntryUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		alterTableAddColumn(
			"AccountEntry", "defaultDeliveryCTermEntryId", "LONG");
		alterTableAddColumn(
			"AccountEntry", "defaultPaymentCTermEntryId", "LONG");
	}
}
```

Correct:

```java
// Remove AccountEntryUpgradeProcess and write the upgrade process as inline
// calls in the registrator class by using the UpgradeProcessFactory class

registry.register(
	"2.5.0", "2.6.0",
	UpgradeProcessFactory.addColumns(
		"AccountEntry", "defaultDeliveryCTermEntryId LONG",
		"defaultPaymentCTermEntryId LONG"));
```

---

Move `alterColumnName`, `alterColumnType`, `alterTableAddColumn` and/or
`alterTableDropColumn` inside `getPostUpgradeSteps` and `getPreUpgradeSteps`
when possible.

### Examples

Incorrect:

```java
@Override
protected void doUpgrade() throws Exception {
	alterTableAddColumn("CPDefinitionLink", "CProductId", "LONG");

	alterColumnName("CPDefinitionLink", "CPDefinitionId1", "CPDefinitionId");

	try (PreparedStatement preparedStatement = connection.prepareStatement(
			"update CPDefinitionLink set CProductId = ? where " +
				"CPDefinitionId2 = ?");
		...
		}
	}

	alterTableDropColumn(
		CPDefinitionLinkModelImpl.TABLE_NAME, "CPDefinitionId2");
}
```

Correct:

```java
@Override
protected void doUpgrade() throws Exception {
	try (PreparedStatement preparedStatement = connection.prepareStatement(
			"update CPDefinitionLink set CProductId = ? where " +
				"CPDefinitionId2 = ?");
		...
		}
	}
}

@Override
protected UpgradeStep[] getPostUpgradeSteps() {
	return new UpgradeStep[] {
		UpgradeProcessFactory.dropColumns(
			CPDefinitionLinkModelImpl.TABLE_NAME, "CPDefinitionId2")
	};
}

@Override
protected UpgradeStep[] getPreUpgradeSteps() {
	return new UpgradeStep[] {
		UpgradeProcessFactory.addColumns(
			"CPDefinitionLink", "CProductId LONG"),
		UpgradeProcessFactory.alterColumnName(
			"CPDefinitionLink", "CPDefinitionId1", "CPDefinitionId")
	};
}
```