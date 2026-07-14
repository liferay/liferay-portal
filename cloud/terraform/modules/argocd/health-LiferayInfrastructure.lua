if ((obj == nil) or (obj.status == nil) or (obj.status.conditions == nil))
then
	return {
		message = "The system is initializing.",
		status = "Progressing"
	}
end

local progressMessage = ""
local ready = false

for _, condition in ipairs(obj.status.conditions)
do
	if (condition.status == "False") and (condition.type == "Ready")
	then
		progressMessage = "Still " .. (condition.reason or "Progressing") .. ": " .. (condition.message or "Not Ready")
	elseif (condition.status == "False") and (condition.type == "Synced")
	then
		return {
			message = condition.message or "Composition pipeline has errors.",
			status = "Degraded"
		}
	elseif (condition.status == "True") and (condition.type == "Ready")
	then
		ready = true
	end
end

if (ready and (obj.status.managedServiceDetailsReady or false))
then
	return {
		message = "The LiferayInfrastructure is healthy.",
		status = "Healthy"
	}
end

return {
	message = progressMessage,
	status = "Progressing"
}