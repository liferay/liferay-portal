#!/usr/bin/env python3

import argparse
import base64
import json
import os
import sys
import urllib.error
import urllib.parse
import urllib.request

BASE_URL = "https://testray.liferay.com"

STATUSES = (
	"DETERMINISTIC",
	"NEAR-DETERMINISTIC",
	"NEW-DETERMINISTIC",
	"INTERMITTENT-HEAVY",
	"INTERMITTENT-LIGHT",
	"NEW-OR-SPORADIC",
)


def classify(fail_count, build_count, streak):
	if fail_count == build_count:
		return "DETERMINISTIC"

	if streak >= 2 and streak == fail_count:
		return "NEW-DETERMINISTIC"

	if fail_count >= build_count - 1:
		return "NEAR-DETERMINISTIC"

	if streak >= 3:
		return "NEW-DETERMINISTIC"

	if fail_count * 2 >= build_count:
		return "INTERMITTENT-HEAVY"

	if fail_count > 1:
		return "INTERMITTENT-LIGHT"

	return "NEW-OR-SPORADIC"


def clean(value):
	return " ".join(str(value or "").split())


def fetch(request):
	try:
		with urllib.request.urlopen(request, timeout=120) as response:
			return json.load(response)
	except urllib.error.HTTPError as error:
		sys.exit("HTTP %s from %s" % (error.code, request.full_url))
	except urllib.error.URLError as error:
		sys.exit("Cannot reach %s: %s" % (request.full_url, error.reason))


def get(token, path, params):
	return fetch(
		urllib.request.Request(
			BASE_URL + path + "?" + urllib.parse.urlencode(params),
			headers={
				"Accept": "application/json",
				"Authorization": "Bearer " + token,
			},
		)
	)


def get_all(token, path, params):
	items = []
	page = 1

	while True:
		data = get(token, path, dict(params, page=page, pageSize=500))

		items.extend(data["items"])

		if page >= data.get("lastPage", 1):
			return items

		page += 1


def get_token():
	client_id = os.environ.get("TESTRAY_CLIENT_ID")
	client_secret = os.environ.get("TESTRAY_CLIENT_SECRET")

	if not client_id or not client_secret:
		sys.exit("TESTRAY_CLIENT_ID and TESTRAY_CLIENT_SECRET must be set.")

	credentials = base64.b64encode(
		(client_id + ":" + client_secret).encode()
	).decode()

	return fetch(
		urllib.request.Request(
			BASE_URL + "/o/oauth2/token",
			data=b"grant_type=client_credentials",
			headers={"Authorization": "Basic " + credentials},
		)
	)["access_token"]


def is_complete(build):
	total = sum(
		build[key] or 0
		for key in (
			"caseResultBlocked",
			"caseResultDidNotRun",
			"caseResultFailed",
			"caseResultInProgress",
			"caseResultPassed",
			"caseResultTestFix",
			"caseResultUntested",
		)
	)

	if not build["caseResultPassed"] or build["caseResultInProgress"]:
		return False

	return (build["caseResultUntested"] or 0) * 10 < total


def odata_string(value):
	return "'%s'" % str(value).replace("'", "''")


def resolve_builds(token, args):
	if args.build:
		newest = get(token, "/o/c/builds/%s" % args.build, {})

		routine_id = newest["r_routineToBuilds_c_routineId"]
	else:
		routines = get(
			token,
			"/o/c/routines",
			{
				"filter": "name eq %s and "
				"r_routineToProjects_c_projectId eq %s"
				% (odata_string(args.routine), odata_string(args.project)),
			},
		)["items"]

		if len(routines) != 1:
			sys.exit(
				"Expected one routine named %s in project %s, found %d."
				% (args.routine, args.project, len(routines))
			)

		newest = None
		routine_id = routines[0]["id"]

	builds = []
	page = 1

	while len(builds) < args.builds:
		data = get(
			token,
			"/o/c/builds",
			{
				"filter": "r_routineToBuilds_c_routineId eq %s"
				% odata_string(routine_id),
				"page": page,
				"pageSize": 50,
				"sort": "dateCreated:desc",
			},
		)

		for build in data["items"]:
			if newest and build["dateCreated"] > newest["dateCreated"]:
				continue

			if not is_complete(build):
				print(
					"Skipping incomplete build %s" % build["name"],
					file=sys.stderr,
				)

				continue

			builds.append(build)

			if len(builds) == args.builds:
				break

		if page >= data.get("lastPage", 1):
			break

		page += 1

	if len(builds) < 3:
		sys.exit("Found %d complete builds; need at least 3." % len(builds))

	if len(builds) < args.builds:
		print(
			"Only %d complete builds were found." % len(builds),
			file=sys.stderr,
		)

	return builds


def main():
	parser = argparse.ArgumentParser(
		description=(
			"Classify every failure in a build by how many of the routine's "
			"recent complete builds it also failed in."
		)
	)

	target = parser.add_mutually_exclusive_group(required=True)

	target.add_argument(
		"--build",
		help="Testray build ID to triage",
	)
	parser.add_argument(
		"--builds",
		default=20,
		help="number of complete builds to profile (at least 3)",
		type=int,
	)
	parser.add_argument(
		"--project",
		default="35392",
		help="Testray project ID that owns the routine (default: master)",
	)
	target.add_argument(
		"--routine",
		help="routine name; triages its newest complete build",
	)
	parser.add_argument("--tsv", help="write one row per failure to this file")

	args = parser.parse_args()

	if args.builds < 3:
		parser.error("--builds must be at least 3")

	token = get_token()

	builds = resolve_builds(token, args)

	if args.build and str(builds[0]["id"]) != str(args.build):
		print(
			"Build %s is incomplete; triaging %s instead."
			% (args.build, builds[0]["name"]),
			file=sys.stderr,
		)

	failures = []

	for build in builds:
		build_failures = {}

		for item in get_all(
			token,
			"/o/c/caseresults",
			{
				"filter": "r_buildToCaseResult_c_buildId eq %s and "
				"(dueStatus eq 'FAILED' or dueStatus eq 'TESTFIX')"
				% odata_string(build["id"]),
			},
		):
			build_failures.setdefault(item["r_caseToCaseResult_c_caseId"], item)

		failures.append(build_failures)

	case_ids = sorted(failures[0])
	names = {}

	for start in range(0, len(case_ids), 100):
		chunk = case_ids[start : start + 100]

		for case in get(
			token,
			"/o/c/cases",
			{
				"filter": "id in (%s)"
				% ",".join(odata_string(case_id) for case_id in chunk),
				"pageSize": 100,
			},
		)["items"]:
			names[case["id"]] = case["name"]

	rows = []

	for case_id in case_ids:
		history = "".join(
			"F" if case_id in build_failures else "."
			for build_failures in failures
		)

		item = failures[0][case_id]
		streak = len(history) - len(history.lstrip("F"))

		rows.append(
			{
				"case_result_id": item["id"],
				"error": clean(item.get("errors"))[:200],
				"fails": history.count("F"),
				"history": history,
				"name": clean(names.get(case_id, case_id)),
				"status": classify(history.count("F"), len(builds), streak),
				"streak": streak,
			}
		)

	rows.sort(
		key=lambda row: (
			STATUSES.index(row["status"]),
			-row["fails"],
			-row["streak"],
			row["name"],
		)
	)

	print("Newest build: %s" % builds[0]["name"])
	print("Profiled builds: %d (history is newest first)" % len(builds))

	for build in builds:
		print("  %s" % build["name"])

	print("Failures in the newest build: %d" % len(rows))
	print(
		"A '.' means the test did not fail in that build: it passed, was "
		"blocked, or did not run. Treat counts as a lower bound."
	)
	print()

	for status in STATUSES:
		print(
			"%-20s %d"
			% (status, sum(1 for row in rows if row["status"] == status))
		)

	if args.tsv:
		columns = (
			"status",
			"fails",
			"streak",
			"history",
			"name",
			"case_result_id",
			"error",
		)

		with open(args.tsv, "w") as tsv:
			tsv.write("\t".join(columns) + "\n")

			for row in rows:
				tsv.write(
					"\t".join(str(row[column]) for column in columns) + "\n"
				)

		print()
		print("Wrote %s" % args.tsv)


if __name__ == "__main__":
	main()