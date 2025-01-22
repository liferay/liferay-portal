//import "lms"
const copyToClipboardButtons = document.querySelectorAll(".copy-to-clipboard-button");

copyToClipboardButtons.forEach((button) => {
  button.addEventListener("click", function (event) {
    const codeToolbar = button.closest('.code-toolbar');

    if (codeToolbar) {
      const codeText = codeToolbar.querySelector('code.language-bash').innerText;

      if (codeText) {
        navigator.clipboard.writeText(codeText)
          .then(function () {
            button.setAttribute('data-copy-state', 'copy-success');

            setTimeout(function () {
              button.setAttribute('data-copy-state', 'copy');
            }, 3000);
          });
      }
    }
  });
});