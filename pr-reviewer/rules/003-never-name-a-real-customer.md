# 003: Never Name a Real Customer

Never write the name, domain, product, team, or any other identifier of a real customer or third party organization into code, test data, configuration, comments, or documentation. When a value only needs to look like a real name or domain, use Liferay's own name or domain (`Liferay`, `liferay.com`) or a reserved example value (`example.com`, `example.org`, `foo.example`, `bar.test`, `Acme`), or randomize it per rule 602 when no assertion checks it.

**Rationale:** Source code outlives the repository it was written in. A private repository gets synced, forked, merged into a public one, pasted into a ticket, or shown in a review, and every copy then reveals who the customer is and what they use. Reserved example values carry no such risk, and using them everywhere keeps test data uniform, so a reader never wonders whether a domain in a test is significant because it belongs to someone.

A violation is a string literal, identifier, comment, or file name in the diff that names a real customer, government body, or company, or a domain, team, or product of one, as sample data, whether in a fixture, an assertion, a mock, or an example. Do not flag `localhost`, loopback and private IP addresses, Liferay's own names and domains, reserved example names and domains (RFC 2606 and RFC 6761: `example.com`, `example.net`, `example.org`, and the `.example`, `.invalid`, `.localhost`, and `.test` top level domains), or the real endpoint of a service the code actually calls, such as `www.googleapis.com` in a Google client.

**Example:** https://github.com/brianchandotcom/liferay-portal-ee/pull/53041#issuecomment-7181028825 flagged unit tests that checked domain validation against a real government customer's domain and team name. The tests should use reserved example values instead.

```diff
-		Assert.assertFalse(FooUtil.isValidDomain("customer gov"));
-		Assert.assertTrue(FooUtil.isValidDomain("customer.gov"));
-		Assert.assertFalse(FooUtil.isValidDomain("customerteam"));
+		Assert.assertFalse(FooUtil.isValidDomain("example com"));
+		Assert.assertTrue(FooUtil.isValidDomain("example.com"));
+		Assert.assertFalse(FooUtil.isValidDomain("example"));
```