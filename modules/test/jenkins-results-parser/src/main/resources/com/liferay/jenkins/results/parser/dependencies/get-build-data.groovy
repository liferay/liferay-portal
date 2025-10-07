import groovy.json.JsonBuilder

import java.util.Date
import java.util.List

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jenkins.metrics.impl.TimeInQueueAction

import hudson.model.Item
import hudson.model.Items
import hudson.model.Job
import hudson.model.ParametersAction
import hudson.model.ParameterValue
import hudson.model.Result
import hudson.model.Run

Date endDate = new Date()

Date startDate = endDate.minus(1)

startDate = Date.parse("yyyyMMdd hh:mm:ss", startDate.format("yyyyMMdd") + " 00:00:00")

List<Object> buildJSONs = []

List<Item> items = Jenkins.instance.allItems

items.each {
	Item item ->

	if (item instanceof Job) {
		List<Run> builds = item.getBuilds()

		builds.each {
			Run build ->

			long startTime = build.getTimeInMillis()

			if ((startTime < startDate.getTime()) || (startTime >= endDate.getTime())) {
				return
			}

			JsonBuilder buildJsonBuilder = new JsonBuilder()
			String buildURL = Jenkins.instance.getRootUrl() + build.getUrl()
			List<Object> parameters = []
			String testrayBuildURL = ""

			if (!buildURL.contains("maintenance") && !buildURL.contains("verification") && !buildURL.contains("-controller") && !buildURL.contains("-propagator")) {
				if ((!buildURL.contains("generate-testray-csv") && !buildURL.contains("-batch") && !buildURL.contains("-downstream")) && (build.getDescription() != null)) {
					Pattern pattern = Pattern.compile("https:\\/\\/testray\\.liferay\\.com.*?[^\"]*")

					Matcher matcher = pattern.matcher(build.getDescription())

					if (matcher.find()) {
						testrayBuildURL = matcher.group()
					}
				}

				ParametersAction parametersAction = build.getAction(hudson.model.ParametersAction.class)

				if (parametersAction != null) {
					List<ParameterValue> parameterValues = parametersAction.getParameters()

					parameterValues.each {
						ParameterValue parameterValue ->

						Object parameterValueObject = parameterValue.getValue()

						if ((parameterValueObject != null) && !parameterValueObject.isEmpty()) {
							String parameterValueName = parameterValue.getName()

							if ((buildURL.contains("-batch") || buildURL.contains("-downstream")) && !(parameterValueName.equals("DIST_PATH") || parameterValueName.equals("JOB_VARIANT"))) {
								return
							}

							JsonBuilder parameterJsonBuilder = new JsonBuilder()

							parameterJsonBuilder name: parameterValueName, value: parameterValueObject

							parameters.add(parameterJsonBuilder.getContent())
						}

					}

				}
			}

			TimeInQueueAction timeInQueueAction = build.getAction(jenkins.metrics.impl.TimeInQueueAction.class)

			long queueDuration = 0

			if (timeInQueueAction != null) {
				queueDuration = timeInQueueAction.getQueuingDurationMillis()
			}

			Result result = build.getResult()

			buildJsonBuilder url: buildURL, startTime: build.getTimeInMillis(), result: result.toString(), duration: build.getDuration(), queueDuration: queueDuration, parameters: parameters, builtOn: build.getBuiltOnStr(), testrayBuildURL: testrayBuildURL

			buildJSONs.add(buildJsonBuilder)
		}
	}
}

println(buildJSONs)

return