package backoff

import (
	"math"
	"time"
)

func Duration(
	consecutiveFailures int32,
	initialDelay time.Duration,
	maxDelay time.Duration,
) time.Duration {
	backoff := float64(initialDelay) * math.Pow(2, float64(max(consecutiveFailures-1, 0)))

	if backoff >= float64(maxDelay) {
		return maxDelay
	}

	return time.Duration(backoff)
}
