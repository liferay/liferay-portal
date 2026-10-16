---

description: Create roles, assign permissions on objects and pages, and manage site memberships via Headless Admin User and object collaborators APIs. Use when the user asks to add a role, grant permissions, make a page private, share an entry with one user, or control who can view or edit an object's entries.
name: manage-roles-permissions

---

# Manage Roles and Permissions

Create roles and assign the minimum required permissions for objects, pages, and sites.

## When to Invoke

- "Create a Reader role", "add a role that can only view Books"
- "Grant edit permissions on this object to Site Members"
- "Make this page visible only to authenticated users"
- "Give the Sales role permission to add and update Orders"
- Called by `build-site` during the roles and ACL phase

## Prerequisites

None for roles and grants. The object collaborators API (sharing an entry with one user) needs feature flag `LPD-17564` on 2026.Q1 and 2026.Q2, where it defaults to off. From 2026.Q3 the flag is gone and collaborators work without it ("Share an Entry With One User" below).

## Workflow

### Create a Role

```bash
curl \
	--data '{
		"name": "<RoleName>",
		"roleType": "regular",
		"name_i18n": {"en_US": "<Role Display Name>"}
	}' \
	--header "Content-Type: application/json" \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-user/v1.0/roles" \
	--user "test@liferay.com:test"
```

`roleType` values:

| Value | Scope |
| --- | --- |
| `regular` | Portal wide; applies across all sites |
| `site` | Site scoped; membership and permissions are site specific |
| `organization` | Organization scoped |

Save the returned `id` as `<role-id>`.

### Assign Users to a Role

```bash
# Assign a user account to a regular role

curl \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-user/v1.0/roles/<role-id>/association/user-account/<user-id>" \
	--user "test@liferay.com:test"
```

For site scoped role assignment, use the role association endpoint scoped to the site (there is **no** `/sites/{id}/site-members` endpoint). The site is addressed by its numeric `<site-id>` here. On a regular site, the association also adds the user as a site member:

```bash
curl \
	--request POST \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-user/v1.0/roles/<role-id>/association/user-account/<user-id>/site/<site-id>" \
	--user "test@liferay.com:test"
```

### Grant Permissions on an Object Definition

Object level permissions control which roles can create, view, update, or delete entries for that object definition.

> **There is no `/permissions` endpoint for an object definition.** `object-admin` declares none (from source), so `PUT /o/object-admin/v1.0/object-definitions/<id>/permissions` cannot work. The definition DTO's `permissions` property sets permissions on the definition itself, not on its entries. Grant object level permissions in the initializer tree with `resource-permissions.json` (the next section and `rules/site-initializer-format.md`), or in Control Panel → Objects → [Object] → Permissions.

> **A role-level grant works over `headless-admin-user` instead:** `PATCH /o/headless-admin-user/v1.0/roles/{roleId}` with only `{"rolePermissions": [{"actionIds": ["ADD_OBJECT_ENTRY"], "primaryKey": "<companyId>", "resourceName": "com.liferay.object#<definitionId>", "scope": 1}]}`. `RoleResourceImpl.patchRole` calls `ResourcePermissionService.addResourcePermission` for each listed action, so it **only adds**: other grants stay, and the role's name, titles and description are kept (omitted fields fall back to the stored ones). Entries with `scope` `4` (individual) are skipped. Read the role first, `GET /roles/{roleId}`, whose `rolePermissions` lists every grant the role holds (`resourceName`, `scope`, `actionIds`), to compare before writing; on the Guest role that is tens of thousands of rows. It cannot revoke: there is no remove path in this method. Verified on 2026.Q2: Guest got company-scope `ADD_OBJECT_ENTRY` on a new object, and an anonymous `GET /o/c/<plural>` still answered `totalCount: 0`.

Common `actionIds` for object definitions:

| Action ID | Meaning |
| --- | --- |
| `ADD_OBJECT_ENTRY` | Create entries |
| `VIEW` | View the object in site and admin UI |
| `PERMISSIONS` | Manage permissions on this object |

### Public Form: Let Guest Create Without Reading

> **A site role never grants the add on a company-scoped object.** The add is checked in group 0, where no site role counts, so a company-scope `ADD_OBJECT_ENTRY` for Site Member renders a Form Container for the site's members and then fails every submission silently. Grant it to a regular role (Guest, User, or a role the members hold). Verified on 2026.Q3; see `manage-form-containers` → "First: Grant `ADD_OBJECT_ENTRY`".

An object registers **two** resource names and the action decides which to grant on — `ADD_OBJECT_ENTRY` is **not** on the `[$OBJECT_DEFINITION_CLASS_NAME:…$]` resource, and granting it there silently does nothing. `rules/guest-access.md` has the table and the `guest-unsupported` set; it is always loaded, so read it there rather than guessing here.

For an anonymous registration, enquiry, or application form, grant creation on the private object and viewing only on the public one. In the initializer tree (`resource-permissions.json`, see `rules/site-initializer-format.md` for the `scope` integers):

```json
[
	{
		"actionIds": ["ADD_OBJECT_ENTRY"],
		"primKey": "0",
		"resourceName": "com.liferay.object#[$OBJECT_DEFINITION_ID:<PrivateObject>$]",
		"roleName": "Guest",
		"scope": "1"
	},
	{
		"actionIds": ["VIEW"],
		"primKey": "0",
		"resourceName": "[$OBJECT_DEFINITION_CLASS_NAME:<PublicObject>$]",
		"roleName": "Guest",
		"scope": "1"
	}
]
```

Do **not** add `VIEW` on the object that receives submissions. Those rows hold whatever the visitor typed — names, emails, addresses — and `VIEW` at company scope publishes all of them to anonymous users. Write only is the correct posture for a public form, and it is why any count or aggregate over that object has to be denormalized onto the public object rather than computed in the browser (see `manage-pages` → "Mapping Limits").

### Grant Permissions on Object Entries

Per entry role grants are the entry's `/permissions` endpoint. To open one entry to one **user**, share it instead ("Share an Entry With One User" below).

The entry `/permissions` PUT, like every Vulcan `/permissions` PUT, takes a **bare JSON array** of Vulcan `Permission` items: `actionIds`, `roleExternalReferenceCode`, `roleName`, `roleType`. The DTO has **no `roleId`** field (from source), and a `{"permissions": [...]}` wrapper returns `400 "Unable to map JSON path"`. Copy the field values from a `GET` on the same endpoint.

The PUT is a **full replace**: read the current grants first and re-send every role that should keep access — Owner and administrator roles included — or they are silently dropped.

```bash
curl \
	--data '[
		{
			"actionIds": ["VIEW", "UPDATE"],
			"roleExternalReferenceCode": "<role-erc>",
			"roleName": "<role-name>",
			"roleType": "regular"
		}
	]' \
	--header "Content-Type: application/json" \
	--request PUT \
	--silent \
	--url "http://localhost:${PORT}/o/c/<pluralLabel>/<entry-id>/permissions" \
	--user "test@liferay.com:test"
```

Common `actionIds` for object entries:

| Action ID | Meaning |
| --- | --- |
| `VIEW` | Read the entry |
| `UPDATE` | Edit the entry |
| `DELETE` | Delete the entry |
| `PERMISSIONS` | Manage permissions on this entry |

### Share an Entry With One User (Collaborators)

Liferay Sharing gives one user access to one object entry, beside the role grants. The CMS's "Share" is this. On 2026.Q1 and 2026.Q2 these endpoints need `LPD-17564` (see "Prerequisites"). Verified on 2026.Q3 on a CMS Space entry:

- **The call:** `PUT /o/c/<plural>/scopes/<scope>/by-external-reference-code/<erc>/collaborators/by-type/User/<userId>` (or `/o/c/<plural>/<id>/collaborators/by-type/User/<userId>`) with `{"actionIds": ["DOWNLOAD", "VIEW"], "type": "User"}`. Sharing actions are `VIEW`, `DOWNLOAD`, `UPDATE`, `ADD_DISCUSSION`; `"share": true` lets the user reshare. `GET` on the same path answers `404` when there is no share.
- **Not by email address:** `…/collaborators/by-email-address/<email>` makes an `Email` collaborator, which may only `VIEW` (`400 "Collaborators of type \"Email\" can only be granted the VIEW action"`), even for an existing user's address.
- **The collection takes no `PUT`.** `POST …/collaborators` replaces the whole list (it deletes collaborators left out).
- **What a share opens:** the entry over REST (by id, by ERC, in the scoped list), its file by its `/documents/` URL (`200` for the shared user; another user `401`, an anonymous visitor `404`), search, and a link to the file held by another object's Attachment field, which follows the file's own entry, not the linking entry.
- **On a custom object's entry (not a CMS file type), `DOWNLOAD` cannot be shared.** `{"actionIds": ["DOWNLOAD", "VIEW"]}` answered `403`, even for the administrator, since the object has no `DOWNLOAD` action. Share `VIEW` alone: a role's company-scope `DOWNLOAD_<FIELD>` grant still gives the shared user the attachment's link. A share's `UPDATE` lets the user PATCH the entry.
- **Every write notifies, an identical one too.** Each `PUT` sends the user a website notification, "<sharer> has shared <title> with you for viewing." (language key `x-has-shared-x-with-you-for-x`), and attempts an email. So compare with the `GET` first and write only on a difference. Unlike the CMS's Space-membership notifications, these render over `headless-user-notification`: an admin can list them (`GET /o/headless-user-notification/v1.0/user-accounts/<userId>/user-notifications`) and mark them read (`PUT /user-notifications/<id>/read`, `204`).
- **A share is indexed as a `SharingEntry`, with no permission filter.** Its document holds the entry's title, dates, `userId` and `toUserId`, and any signed-in user's `/o/search/v1.0/search` finds that title and those dates (no link, no content). Leave `com.liferay.sharing.model.SharingEntry` out of a blueprint's searchable types where the titles matter.
- **Deleting the entry** deletes its shares, their search documents and their notifications. A CMS entry's first `DELETE` only moves it to the Space's trash (`status` 8, still readable by ERC); a second `DELETE` removes it.

### Depot Roles (Spaces and Asset Libraries)

A Space or asset library has its own role type, `roleType: depot` (`role_.type_` 5). Two of these roles are given implicitly by `DepotRoleContributor` (from source):

| Role | ERC | Who gets it |
| --- | --- | --- |
| Asset Library Member | `L_ASSET_LIBRARY_MEMBER` | Members of the library or Space itself |
| Asset Library Connected Site Member | `L_ASSET_LIBRARY_CONNECTED_SITE_MEMBER` | Members of any Site the library is connected to, **including members only through a user group** (the user bag counts inherited groups) |

Grant the second one on items and folders to say "whoever belongs to the connected Site", with the same bare `Permission[]` body (`"roleType": "depot"`). Space entries also start with a `User` VIEW grant, which opens them to every signed-in user, so a restricted audience means a full replace without it.

### Grant Permissions on a Site Page

Page level permissions control visibility and edit access.

Same body shape as for object entries: a bare `Permission[]` array (`putSiteSitePagePermissionsPage`, from source). Same full replace semantics.

```bash
curl \
	--data '[
		{
			"actionIds": ["VIEW"],
			"roleExternalReferenceCode": "<guest-role-erc>",
			"roleName": "Guest",
			"roleType": "regular"
		},
		{
			"actionIds": ["VIEW", "UPDATE"],
			"roleExternalReferenceCode": "<role-erc>",
			"roleName": "<role-name>",
			"roleType": "regular"
		}
	]' \
	--header "Content-Type: application/json" \
	--request PUT \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-site/v1.0/sites/<site-erc>/site-pages/<page-erc>/permissions" \
	--user "test@liferay.com:test"
```

To make a page visible only to authenticated users, remove the VIEW permission from the Guest role. To make it fully private, also remove it from the User role and grant VIEW only to the target role.

### Verify Roles and Permissions

```bash
# List roles

curl \
	--silent \
	--url "http://localhost:${PORT}/o/headless-admin-user/v1.0/roles" \
	--user "test@liferay.com:test" \
	| jq '[.items[] | {id, name, roleType}]'

# Check permissions on an object entry (object definitions have no /permissions endpoint)

curl \
	--silent \
	--url "http://localhost:${PORT}/o/c/<pluralLabel>/<entry-id>/permissions" \
	--user "test@liferay.com:test" \
	| jq '.items'
```

**Checking a user's roles client-side: they are scoped, not flat.** `GET /o/headless-admin-user/v1.0/my-user-account` returns roles split across `roleBriefs` (regular), `accountBriefs[].roleBriefs`, `organizationBriefs[].roleBriefs`, `siteBriefs[].roleBriefs`, and `userGroupBriefs[].roleBriefs`. Code that checks only the top-level `roleBriefs` misses every account/site/org role — flatten all five before testing for a role name. Also note a plain account **member** shows an empty `accountBriefs[].roleBriefs` (membership is implicit, not a role).

**`roleBriefs` lists only the roles the caller can view, and a brief's `name` is the role's title.** `UserResourceDTOConverter._toRoleBriefs` drops every role the caller lacks `VIEW` on (`:522–537`), and `RoleBriefUtil` sets `name` to the title and `key` to the role's name (`:25–32`). Verified on 2026.Q3: a user's own `my-user-account` listed only `User` although the user held another regular role, and listed it as `{"name": "<role title>", "key": "<role name>"}` once that role got `VIEW` on itself at individual scope. An administrator sees every role. So code that reads a user's roles with that user's own token needs that `VIEW`, and must match titles or read `key`.

## Permission Design Principles

- Assign the minimum set of `actionIds` that satisfies the workflow requirement.
- Use site roles (not regular roles) for permissions that should vary per site.
- The Guest role represents unauthenticated visitors. Remove VIEW from Guest to require login.
- The User role represents any authenticated user. Remove VIEW from User and grant to a specific role to restrict access.
- **Company-scope grants on `AccountEntry` read no account-restricted entry.** A regular role holding `VIEW`, `UPDATE` and the user actions on every account still gets `404` on an account-restricted object's entries: the restriction admits the account's members and administrators, not holders of account permissions. Give such a role entry access by membership, or by an explicit object grant, which then opens every account's entries to it. Verified on 2026.Q2.
- **A Site Administrator or Site Owner needs no grant on a site-scoped object, and none can be withheld.** They are group admins, and `AdvancedPermissionChecker` answers `true` for a group admin on any resource of the group after the role check fails. Verified on 2026.Q2 with a user given only the site role Site Administrator, no membership and no grant: `GET` showed every action (`update`, `delete`, `permissions`, `share`), `PATCH` and `DELETE` worked on an entry another user owns, and **`POST` answered `200` without `ADD_OBJECT_ENTRY`**. Only `readOnly` fields bind them, and those not on create. Removing a grant from these roles therefore changes nothing; route such writes through a module if they must be checked.

## Creating Users via API — Clear Password and Terms Flags

A user created through the Headless Admin User API must reset the password and accept the terms of use at first login. Until then, the new user's REST calls return silent 403s.

The headless `UserAccount` DTO cannot clear those flags: it has no `agreedToTermsOfUse` or `passwordReset` property (from source), and a create that sends them returns `400 "The properties [passwordReset,agreedToTermsOfUse] are unknown"` (verified on 2026.Q3). The reliable way to avoid the first login 403 trap is the preboot bootstrap (`terms.of.use.required=false`, `passwords.default.policy.change.required=false`) covered in `workspace-init`.

## Success Signal

TODO / inferred — verify against a running bundle. The Verify Roles and Permissions step above (role listed; granted `actionIds` present; a user holding only that role is allowed the granted actions and denied the rest) is the observable completion check; confirm on a live bundle.