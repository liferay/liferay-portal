if ((obj == nil) or (obj.status == nil) or (obj.status.health == nil) or (obj.status.health.status == nil))
then
	return {
		message = "",
		status = "Progressing"
	}
end

return {
	message = obj.status.health.message or "",
	status = obj.status.health.status
}