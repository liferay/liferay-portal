if ((obj == nil) or (obj.status == nil) or (obj.status.conditions == nil))
then
	return {
		message = "The DXP operator has not reconciled the client extension yet.",
		status = "Progressing"
	}
end

for _, condition in ipairs(obj.status.conditions)
do
	if (condition.type == "Ready") and (condition.status == "True")
	then
		return {
			message = condition.message or "The client extension is ready.",
			status = "Healthy"
		}
	end
end

if (obj.status.phase == "Degraded")
then
	for _, condition in ipairs(obj.status.conditions)
	do
		if (condition.status == "False")
		then
			return {
				message = condition.type .. ": " .. (condition.message or condition.reason or "False"),
				status = "Degraded"
			}
		end
	end

	return {
		message = "The client extension is degraded.",
		status = "Degraded"
	}
end

return {
	message = "Waiting for the client extension to be ready.",
	status = "Progressing"
}
