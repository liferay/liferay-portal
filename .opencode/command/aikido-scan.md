---
description: Run the project Aikido local scan workflow
---

# Aikido Scan

Read the `aikido-scan` skill and run its workflow for this project using `$ARGUMENTS` as additional scan context or requested flags.

Follow the skill's safety rules exactly: check workspace state first, preview cleanup before any `--apply`, require Aikido scanner credentials in the OS environment, and do not read `.env` for secrets.
