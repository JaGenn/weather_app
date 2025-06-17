function validatePasswords() {
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const mismatchElement = document.getElementById('passwordMismatch');

        if (password !== confirmPassword) {
            mismatchElement.style.display = 'block';
            document.getElementById('confirmPassword').classList.add('is-invalid');
            return false;
        }
        return true;
    }

    document.getElementById('confirmPassword').addEventListener('input', function() {
        const password = document.getElementById('password').value;
        const confirmPassword = this.value;
        const mismatchElement = document.getElementById('passwordMismatch');

        if (password !== confirmPassword) {
            mismatchElement.style.display = 'block';
            this.classList.add('is-invalid');
        } else {
            mismatchElement.style.display = 'none';
            this.classList.remove('is-invalid');
        }
    });