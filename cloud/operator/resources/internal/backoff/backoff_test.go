package backoff

import (
	"testing"
	"time"
)

func TestDuration(t *testing.T) {
	initialDelay := 30 * time.Second
	maxDelay := 30 * time.Minute

	testCases := map[string]struct {
		consecutiveFailures int32
		expected            time.Duration
	}{
		"caps backoff at the maximum": {
			consecutiveFailures: 20,
			expected:            maxDelay,
		},
		"first failure uses the initial delay": {
			consecutiveFailures: 1,
			expected:            initialDelay,
		},
		"second failure doubles the initial delay": {
			consecutiveFailures: 2,
			expected:            2 * initialDelay,
		},
		"third failure quadruples the initial delay": {
			consecutiveFailures: 3,
			expected:            4 * initialDelay,
		},
		"zero failures uses the initial delay": {
			consecutiveFailures: 0,
			expected:            initialDelay,
		},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			if actual := Duration(
				testCase.consecutiveFailures, initialDelay, maxDelay,
			); actual != testCase.expected {
				t.Errorf("Duration = %s, want %s", actual, testCase.expected)
			}
		})
	}
}
