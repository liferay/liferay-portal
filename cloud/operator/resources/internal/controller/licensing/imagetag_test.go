package licensing

import "testing"

func TestImageTag(t *testing.T) {
	testCases := map[string]struct {
		image string
		want  string
	}{
		"a digest":               {image: "liferay/dxp@sha256:abc123", want: ""},
		"a plain tag":            {image: "liferay/dxp:2026.q3.0", want: "2026.q3.0"},
		"a plain tag with lts":   {image: "liferay/dxp:2026.q1.11-lts", want: "2026.q1.11-lts"},
		"a port and no tag":      {image: "registry:5000/liferay/dxp", want: ""},
		"a registry with a port": {image: "registry:5000/liferay/dxp:2026.q3.0", want: "2026.q3.0"},
		"a tag and a digest":     {image: "liferay/dxp:2026.q3.0@sha256:abc123", want: "2026.q3.0"},
		"no tag at all":          {image: "liferay/dxp", want: ""},
	}

	for name, testCase := range testCases {
		t.Run(name, func(t *testing.T) {
			if got := imageTag(testCase.image); got != testCase.want {
				t.Errorf("imageTag(%q) = %q, want %q", testCase.image, got, testCase.want)
			}
		})
	}
}
