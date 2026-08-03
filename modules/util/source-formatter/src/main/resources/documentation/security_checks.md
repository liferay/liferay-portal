# Security Checks

Check | File Extensions | Description
----- | --------------- | -----------
FIPSTLSVerificationCheck | .java | Finds outbound TLS verification bypasses that are not guarded by `PropsValues.FIPS_ENABLED`, see LPD-93649. |
JSPXSSVulnerabilitiesCheck | .jsp, .jspf, .jspx, .tag, .tpl, or .vm | Finds xss vulnerabilities. |
JavaDeserializationSecurityCheck | .java | Finds Java serialization vulnerabilities. |
JavaXMLSecurityCheck | .java | Finds possible XXE or Quadratic Blowup security vulnerabilities. |