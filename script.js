fetch('http://localhost:8080/api/patients')
  .then(response => response.json())
  .then(data => {
    const patientList = document.getElementById('patient-list');
    patientList.innerHTML = ''; // Clear previous content
    data.forEach(patient => {
      const item = document.createElement('div');
      item.textContent = `${patient.name} (${patient.email}, ${patient.phone})`;
      patientList.appendChild(item);
    });
  })
  .catch(error => {
    console.error('Error fetching patients:', error);
  });
    // Show the dashboard layout (sidebar is now in index.html)
    function injectDashboardLayout() {
        // Just show the dashboard view and set up navigation
        const dashboardView = document.getElementById('staff-dashboard-view');
        if (!dashboardView) return;
        dashboardView.classList.remove('hidden');
        setupSidebarNavigation();
        // Attach logout handler (always use onclick to avoid duplicate listeners)
        const logoutBtn = document.getElementById('logout-btn');
        if (logoutBtn) {
            logoutBtn.onclick = logout;
        }
        // Load dashboard by default after login
        const dashboardSection = document.getElementById('dashboard-section');
        const mainContent = document.getElementById('main-content');
        if (dashboardSection && mainContent) {
            // Only inject the real dashboard content, not the wrapper
            const dashboardContent = dashboardSection.querySelector('#dashboard');
            if (dashboardContent) {
                mainContent.innerHTML = dashboardContent.innerHTML;
                setupDashboard();
            }
        }
    }

    // Setup sidebar navigation for SPA using inlined hidden sections
    function setupSidebarNavigation() {
        const sidebarLinks = document.querySelectorAll('.sidebar-link');
        const mainContent = document.getElementById('main-content');
        sidebarLinks.forEach(link => {
            link.addEventListener('click', function (e) {
                const hash = link.getAttribute('href');
                e.preventDefault();
                sidebarLinks.forEach(l => l.classList.remove('active', 'bg-sky-100', 'text-sky-600'));
                link.classList.add('active', 'bg-sky-100', 'text-sky-600');
                let sectionId = '';
                if (hash === '#dashboard') {
                    sectionId = 'dashboard-section';
                } else if (hash === '#appointments') {
                    sectionId = 'appointments-section';
                } else if (hash === '#patients') {
                    sectionId = 'patients-section';
                } else if (hash === '#check-in') {
                    sectionId = 'check-in-section';
                } else if (hash === '#queue') {
                    sectionId = 'queue-section';
                } else if (hash === '#reports') {
                    sectionId = 'reports-section';
                }
                if (sectionId) {
                    const section = document.getElementById(sectionId);
                    if (section) {
                        // For dashboard, inject only the dashboard content
                        if (sectionId === 'dashboard-section') {
                            const dashboardContent = section.querySelector('#dashboard');
                            if (dashboardContent) {
                                mainContent.innerHTML = dashboardContent.innerHTML;
                                setupDashboard();
                            }
                        } else {
                            const main = section.querySelector('main');
                            mainContent.innerHTML = main ? main.outerHTML : '';
                        }
                    }
                }
                // Always re-attach logout handler after navigation
                const logoutBtn = document.getElementById('logout-btn');
                if (logoutBtn) {
                    logoutBtn.onclick = logout;
                }
            });
        });
        setTimeout(() => {
            const logoutBtn = document.getElementById('logout-btn');
            if (logoutBtn) {
                logoutBtn.onclick = logout;
            }
        }, 0);
    }
// Removed DOMContentLoaded wrapper so code runs immediately

// --- Patient Management Section: Fetch backend data and merge with mock data ---

// --- Patient Management Section: Fetch backend data and merge with mock data, allow admin actions ---
let allPatients = [];
let mockPatients = [
    { name: 'Sarah Johnson', studentId: '220154789', email: 'sarah.johnson@cput.ac.za', phone: '+27 82 123 4567', residence: 'Bellville', condition: 'Flu', status: 'Waiting', source: 'mock' },
    { name: 'David Chen', studentId: '219876543', email: 'david.chen@cput.ac.za', phone: '+27 82 987 6543', residence: 'Cape Town', condition: 'Allergy', status: 'Waiting', source: 'mock' },
    { name: 'Maria Garcia', studentId: '221122334', email: 'maria.garcia@cput.ac.za', phone: '+27 82 555 1234', residence: 'Wellington', condition: 'Check-up', status: 'Waiting', source: 'mock' },
    { name: 'James Smith', studentId: '218765123', email: 'james.smith@cput.ac.za', phone: '+27 82 321 4567', residence: 'Mowbray', condition: 'General Consultation', status: 'Waiting', source: 'mock' }
];

function fetchAndRenderPatients() {
    fetch('http://localhost:8080/api/patients')
        .then(response => response.json())
        .then(data => {
            // Mark backend patients with source 'backend'
            const backendPatients = (data || []).map(p => ({ ...p, source: 'backend' }));
            allPatients = [...mockPatients, ...backendPatients];
            renderPatients(allPatients);
        })
        .catch(error => {
            // If backend fails, just show mock data
            allPatients = [...mockPatients];
            renderPatients(allPatients);
            console.error('Error fetching backend patients:', error);
        });
}

function renderPatients(patients) {
    const list = document.getElementById('patient-list');
    if (!list) return;
    list.innerHTML = '';
    patients.forEach((p, i) => {
        const card = document.createElement('div');
        card.className = 'bg-white rounded-xl shadow-md p-6 flex flex-col md:flex-row md:items-center md:justify-between';
        card.innerHTML = `
            <div>
                <div class="font-bold text-lg text-sky-800">${p.name || ''}</div>
                <div class="text-slate-500 text-sm">Student ID: ${p.studentId || ''}</div>
                <div class="text-slate-500 text-sm">Email: ${p.email || ''}</div>
                <div class="text-slate-500 text-sm">Phone: ${p.phone || ''}</div>
                <div class="text-slate-500 text-sm">Residence: ${p.residence || ''}</div>
                <div class="text-slate-500 text-sm">Last Condition: ${p.condition || ''}</div>
                <div class="text-slate-500 text-sm">Status: <span class="font-semibold">${p.status || ''}</span></div>
            </div>
            <div class="mt-4 md:mt-0 flex space-x-2">
                <button class="status-btn bg-green-500 text-white px-4 py-2 rounded-lg font-semibold hover:bg-green-600" data-index="${i}" data-status="Completed">Mark Completed</button>
                <button class="status-btn bg-yellow-500 text-white px-4 py-2 rounded-lg font-semibold hover:bg-yellow-600" data-index="${i}" data-status="Waiting">Mark Waiting</button>
                <button class="call-btn bg-sky-500 text-white px-4 py-2 rounded-lg font-semibold hover:bg-sky-600" data-index="${i}">Call Next</button>
                <button class="remove-btn bg-red-500 text-white px-4 py-2 rounded-lg font-semibold hover:bg-red-600" data-index="${i}">Remove</button>
            </div>
        `;
        list.appendChild(card);
    });
}

// Search patients by name, studentId, or email (all, including completed)
document.addEventListener('DOMContentLoaded', function () {
    fetchAndRenderPatients();
    const list = document.getElementById('patient-list');
    const searchInput = document.getElementById('patient-search-input');
    if (searchInput) {
        searchInput.addEventListener('input', function(e) {
            const term = e.target.value.toLowerCase();
            const filtered = allPatients.filter(p =>
                (p.name && p.name.toLowerCase().includes(term)) ||
                (p.studentId && p.studentId.toLowerCase().includes(term)) ||
                (p.email && p.email.toLowerCase().includes(term))
            );
            renderPatients(filtered);
        });
    }
    if (list) {
        list.addEventListener('click', function(e) {
            const idx = e.target.dataset.index;
            const patient = allPatients[idx];
            if (!patient) return;
            if (e.target.classList.contains('status-btn')) {
                // Update status in backend if patient is from backend
                if (patient.source === 'backend') {
                    fetch(`http://localhost:8080/api/patients/${patient.id}`, {
                        method: 'PUT',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ ...patient, status: e.target.dataset.status })
                    })
                    .then(res => res.json())
                    .then(updated => {
                        allPatients[idx].status = updated.status;
                        renderPatients(allPatients);
                    })
                    .catch(err => alert('Failed to update status.'));
                } else {
                    // For mock data, just update locally
                    allPatients[idx].status = e.target.dataset.status;
                    renderPatients(allPatients);
                }
            } else if (e.target.classList.contains('call-btn')) {
                alert('Calling next patient (demo only)');
            } else if (e.target.classList.contains('remove-btn')) {
                if (confirm('Remove this patient?')) {
                    if (patient.source === 'backend') {
                        fetch(`http://localhost:8080/api/patients/${patient.id}`, {
                            method: 'DELETE'
                        })
                        .then(() => {
                            allPatients.splice(idx, 1);
                            renderPatients(allPatients);
                        })
                        .catch(err => alert('Failed to delete patient.'));
                    } else {
                        allPatients.splice(idx, 1);
                        renderPatients(allPatients);
                    }
                }
            }
        });
    }
});
    const appState = {
        currentUserRole: null, // 'admin' or 'nurse'
    };

    const mockData = {
        appointments: [
            { name: 'Sarah Johnson', studentId: '220154789', time: '09:00 AM', type: 'General Consultation', status: 'confirmed' },
            { name: 'David Chen', studentId: '219876543', time: '09:30 AM', type: 'Follow-up', status: 'confirmed' },
            { name: 'Maria Garcia', studentId: '221122334', time: '10:00 AM', type: 'Flu Shot', status: 'pending' },
            { name: 'James Smith', studentId: '218765123', time: '10:15 AM', type: 'General Consultation', status: 'confirmed' },
        ],
        stats: {
            totalPatients: 2847,
            todaysAppointments: 24,
            avgWaitTime: 18,
            checkInsToday: 18,
        },
        chartData: {
            labels: ['Confirmed', 'Pending', 'Cancelled'],
            data: [18, 5, 1],
        }
    };

    showView('role-selection-view');

    document.getElementById('student-btn')?.addEventListener('click', () => {
        showView('student-booking-view');
    });

    document.getElementById('staff-btn')?.addEventListener('click', () => {
        showView('staff-login-view');
    });

    document.querySelectorAll('.back-button').forEach(btn => {
        btn.addEventListener('click', () => {
            showView('role-selection-view');
        });
    });

    // Attach login form event listener ONCE on page load
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
                loginForm.addEventListener('submit', function(e) {
                        e.preventDefault();
                        const username = e.target.username.value;
                        const password = e.target.password.value;
                        const errorEl = document.getElementById('login-error');

                        if (username === 'admin' && password === 'admin123') {
                                appState.currentUserRole = 'admin';
                                errorEl.classList.add('hidden');
                                showView('staff-dashboard-view');
                                setTimeout(() => {
                                    injectDashboardLayout();
                                    setupUserRoleUI();
                                }, 0);
                        } else if (username === 'nurse' && password === 'nurse123') {
                                appState.currentUserRole = 'nurse';
                                errorEl.classList.add('hidden');
                                showView('staff-dashboard-view');
                                setTimeout(() => {
                                    injectDashboardLayout();
                                    setupUserRoleUI();
                                }, 0);
                        } else {
                                errorEl.classList.remove('hidden');
                        }
                });
    }

    // View switching
    function showView(viewId) {
        const views = ['role-selection-view', 'student-booking-view', 'staff-login-view', 'staff-dashboard-view'];
        views.forEach(id => {
            const el = document.getElementById(id);
            if (el) el.classList.add('hidden');
        });
        const target = document.getElementById(viewId);
        if (target) target.classList.remove('hidden');
    }

  // Initial view
    // Optionally expose navigate for sidebar clicks
    window.navigate = navigate;


    function navigate(hash) {
        // This function handles navigation within the staff dashboard.
        const targetId = hash.substring(1);
        
        document.querySelectorAll('.content-section').forEach(section => {
            section.classList.remove('active');
        });
        const targetSection = document.getElementById(targetId);
        if (targetSection) {
            targetSection.classList.add('active');
        }

        document.querySelectorAll('.sidebar-link').forEach(link => {
            link.classList.remove('active');
            if (link.getAttribute('href') === hash) {
                link.classList.add('active');
            }
        });
    }

    function setupDashboard() {
        // This function populates the mock data on the dashboard.
        const appointmentsList = document.getElementById('appointments-list');
        appointmentsList.innerHTML = '';
        mockData.appointments.forEach(appt => {
            const statusClass = appt.status === 'confirmed' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800';
            const item = `
                <div class="flex items-center justify-between p-3 rounded-lg hover:bg-slate-50">
                    <div class="flex items-center">
                        <div class="w-10 h-10 rounded-full bg-sky-100 text-sky-700 flex items-center justify-center font-bold mr-4">${appt.name.charAt(0)}</div>
                        <div>
                            <p class="font-semibold text-slate-800">${appt.name}</p>
                            <p class="text-sm text-slate-500">${appt.studentId}</p>
                        </div>
                    </div>
                    <div class="text-right">
                        <p class="font-medium text-slate-700">${appt.time}</p>
                        <p class="text-sm px-2 py-1 rounded-full inline-block mt-1 ${statusClass}">${appt.status}</p>
                    </div>
                </div>
            `;
            appointmentsList.insertAdjacentHTML('beforeend', item);
        });
        
        const ctx = document.getElementById('appointmentsChart').getContext('2d');
        if (window.appointmentsChart instanceof Chart) {
            window.appointmentsChart.destroy();
        }
        window.appointmentsChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: mockData.chartData.labels,
                datasets: [{
                    label: 'Appointments',
                    data: mockData.chartData.data,
                    backgroundColor: ['#38bdf8', '#facc15', '#f87171'],
                    borderColor: '#ffffff',
                    borderWidth: 4,
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '70%',
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            boxWidth: 12,
                            padding: 20,
                        }
                    },
                    tooltip: {
                        enabled: true
                    }
                }
            }
        });
    }

    function setupUserRoleUI() {
        // Hides the "Patients" link for non-admin users.
        const patientsLink = document.getElementById('patients-link');
        if (patientsLink) {
            if (appState.currentUserRole === 'admin') {
                patientsLink.style.display = 'flex';
            } else {
                patientsLink.style.display = 'none';
            }
        }
    }

    function logout() {
        // Prevent multiple modals
        if (document.getElementById('logout-modal')) return;
        // Show confirmation modal before logging out
        const modal = document.createElement('div');
        modal.id = 'logout-modal';
        modal.className = 'fixed inset-0 bg-slate-900 bg-opacity-50 flex items-center justify-center z-50';
        modal.innerHTML = `
            <div class="bg-white p-8 rounded-lg shadow-xl text-center max-w-sm w-full">
                <h3 class="text-xl font-bold text-red-700 mb-4">Confirm Logout</h3>
                <p class="text-slate-600 mb-6">Are you sure you want to log out?</p>
                <button id="confirm-logout-btn" class="bg-red-600 text-white font-semibold py-2 px-6 rounded-lg hover:bg-red-700 transition duration-300 mr-2">Logout</button>
                <button id="cancel-logout-btn" class="bg-slate-200 text-slate-700 font-semibold py-2 px-6 rounded-lg hover:bg-slate-300 transition duration-300">Cancel</button>
            </div>
        `;
        document.body.appendChild(modal);
        document.getElementById('confirm-logout-btn').onclick = function() {
            appState.currentUserRole = null;
            // Hide dashboard and show role selection
            showView('role-selection-view');
            // Optionally clear dashboard content
            const dashboardView = document.getElementById('staff-dashboard-view');
            if (dashboardView) dashboardView.innerHTML = '';
            modal.remove();
        };
        document.getElementById('cancel-logout-btn').onclick = function() {
            modal.remove();
        };
        // Trap focus inside modal
        const focusable = modal.querySelectorAll('button');
        let focusIdx = 0;
        modal.addEventListener('keydown', e => {
            if (e.key === 'Tab') {
                e.preventDefault();
                focusIdx = (focusIdx + (e.shiftKey ? -1 : 1) + focusable.length) % focusable.length;
                focusable[focusIdx].focus();
            } else if (e.key === 'Escape') {
                modal.remove();
            }
        });
    }

    // Attach event listener for booking form only (login form handled in showView)

    // Student booking form handler (matches new structure)
    const studentBookingSubmit = document.getElementById('student-booking-submit');
    if (studentBookingSubmit) {
        studentBookingSubmit.addEventListener('click', function(e) {
            e.preventDefault();
            // Gather all student booking form values
            const studentNumber = document.getElementById('studentNumber').value;
            const fullName = document.getElementById('fullName').value;
            const email = document.getElementById('email').value;
            const symptoms = document.getElementById('symptoms').value;
            const serviceType = document.getElementById('serviceType').value;
            const preferredDate = document.getElementById('preferredDate').value;
            const preferredTime = document.getElementById('preferredTime').value;
            // Validate required fields (except time)
            if (!studentNumber || !fullName || !email || !serviceType || !preferredDate) {
                alert('Please fill in all required fields.');
                return;
            }
            // Prepare patient object for backend (fields not in form set as null)
            const patientData = {
                name: fullName,
                studentId: studentNumber,
                email: email,
                phone: null, // not collected in this form
                residence: null, // not collected in this form
                condition: symptoms || null // use symptoms as last condition
            };
            fetch('http://localhost:8080/api/patients', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(patientData)
            })
            .then(response => {
                if (!response.ok) throw new Error('Failed to save patient');
                return response.json();
            })
            .then(savedPatient => {
                // Show confirmation modal
                const bookingMessage = document.createElement('div');
                bookingMessage.className = 'fixed inset-0 bg-slate-900 bg-opacity-50 flex items-center justify-center p-4 z-50';
                bookingMessage.innerHTML = `
                    <div class="bg-white p-8 rounded-lg shadow-xl text-center max-w-sm w-full">
                        <h3 class="text-xl font-bold text-sky-800 mb-4">Appointment Booked!</h3>
                        <p class="text-slate-600 mb-6">Your appointment has been successfully submitted. A confirmation email has been sent to <span class='font-semibold text-sky-700'>${email}</span>.</p>
                        <button onclick="this.parentElement.parentElement.remove()" class="bg-sky-600 text-white font-semibold py-2 px-6 rounded-lg hover:bg-sky-700 transition duration-300">OK</button>
                    </div>
                `;
                document.body.appendChild(bookingMessage);
                // Reset all fields in both forms
                document.getElementById('booking-form').reset();
                document.getElementById('appointment-details-form').reset();
            })
            .catch(error => {
                alert('There was an error booking your appointment. Please try again.');
                console.error(error);
            });
        });
    }

    // Listener for the URL hash to enable dashboard navigation
    window.addEventListener('hashchange', () => {
        if(document.getElementById('staff-dashboard-view').classList.contains('hidden') === false) {
            navigate(window.location.hash || '#dashboard');
        }
    });

    // Click handlers for the sidebar links
    document.querySelectorAll('.sidebar-link').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const hash = e.currentTarget.getAttribute('href');
            window.location.hash = hash;
        });
    });

    // New Gemini API functions
    async function generateMedicalSummary(prompt) {
        const summaryOutputEl = document.getElementById('summary-output');
        const summaryTextEl = document.getElementById('summary-text');
        const loadingIndicatorEl = document.getElementById('loading-indicator');

        summaryOutputEl.classList.remove('hidden');
        summaryTextEl.textContent = '';
        loadingIndicatorEl.classList.remove('hidden');

        let chatHistory = [];
        const fullPrompt = `You are a medical assistant. Provide a brief and easy-to-understand summary of the following medical topic, patient history, or symptom description. Do not include any disclaimers or conversational text. Just give the summary. The input is: "${prompt}"`;
        chatHistory.push({ role: "user", parts: [{ text: fullPrompt }] });
        
        // Exponential backoff for retries
        let delay = 1000;
        const maxRetries = 5;
        for (let i = 0; i < maxRetries; i++) {
            try {
                const payload = { contents: chatHistory };
                const apiKey = ""
                const apiUrl = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-05-20:generateContent?key=${apiKey}`;
                const response = await fetch(apiUrl, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                if (!response.ok) {
                    if (response.status === 429) { // Too Many Requests
                        await new Promise(res => setTimeout(res, delay));
                        delay *= 2;
                        continue;
                    } else {
                        throw new Error(`API call failed with status: ${response.status}`);
                    }
                }
                
                const result = await response.json();
                if (result.candidates && result.candidates.length > 0 &&
                    result.candidates[0].content && result.candidates[0].content.parts &&
                    result.candidates[0].content.parts.length > 0) {
                    const text = result.candidates[0].content.parts[0].text;
                    summaryTextEl.textContent = text;
                    document.getElementById('read-aloud-btn').classList.remove('hidden');
                } else {
                    summaryTextEl.textContent = 'Could not generate a summary. Please try again with a different query.';
                }
                loadingIndicatorEl.classList.add('hidden');
                return;
            } catch (error) {
                console.error('API call error:', error);
                summaryTextEl.textContent = 'An error occurred. Please try again.';
                loadingIndicatorEl.classList.add('hidden');
                return;
            }
        }
        // If all retries fail
        summaryTextEl.textContent = 'Failed to get a response after multiple retries. Please try again later.';
        loadingIndicatorEl.classList.add('hidden');
    }

    // TTS Functions
    function base64ToArrayBuffer(base64) {
        const binaryString = window.atob(base64);
        const len = binaryString.length;
        const bytes = new Uint8Array(len);
        for (let i = 0; i < len; i++) {
            bytes[i] = binaryString.charCodeAt(i);
        }
        return bytes.buffer;
    }

    function pcmToWav(pcm16, sampleRate) {
        const dataView = new DataView(new ArrayBuffer(44 + pcm16.length * 2));
        let offset = 0;

        // RIFF identifier
        function writeString(str) {
            for (let i = 0; i < str.length; i++) {
                dataView.setUint8(offset++, str.charCodeAt(i));
            }
        }

        // RIFF chunk descriptor
        writeString('RIFF');
        dataView.setUint32(offset, 36 + pcm16.length * 2, true); offset += 4;
        writeString('WAVE');

        // fmt chunk
        writeString('fmt ');
        dataView.setUint32(offset, 16, true); offset += 4; // Sub-chunk size
        dataView.setUint16(offset, 1, true); offset += 2; // Audio format (1 = PCM)
        dataView.setUint16(offset, 1, true); offset += 2; // Number of channels
        dataView.setUint32(offset, sampleRate, true); offset += 4; // Sample rate
        dataView.setUint32(offset, sampleRate * 2, true); offset += 4; // Byte rate
        dataView.setUint16(offset, 2, true); offset += 2; // Block align
        dataView.setUint16(offset, 16, true); offset += 2; // Bits per sample

        // data chunk
        writeString('data');
        dataView.setUint32(offset, pcm16.length * 2, true); offset += 4;
        
        // Write the PCM data
        for (let i = 0; i < pcm16.length; i++, offset += 2) {
            dataView.setInt16(offset, pcm16[i], true);
        }
        
        return new Blob([dataView], { type: 'audio/wav' });
    }

    async function generateTTS(text) {
        const audioContext = new (window.AudioContext || window.webkitAudioContext)();
        const payload = {
            contents: [{
                parts: [{ text: text }]
            }],
            generationConfig: {
                responseModality: "AUDIO",
                speechConfig: {
                    voiceConfig: {
                        prebuiltVoiceConfig: { voiceName: "Kore" }
                    }
                }
            },
            model: "gemini-2.5-flash-preview-tts"
        };

        const apiKey = ""; // Leave as-is
        const apiUrl = `https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-tts:generateContent?key=${apiKey}`;

        const readAloudBtn = document.getElementById('read-aloud-btn');
        readAloudBtn.textContent = 'Loading...';
        readAloudBtn.disabled = true;

        try {
            const response = await fetch(apiUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const result = await response.json();
            const part = result?.candidates?.[0]?.content?.parts?.[0];
            const audioData = part?.inlineData?.data;
            const mimeType = part?.inlineData?.mimeType;

            if (audioData && mimeType && mimeType.startsWith("audio/")) {
                const sampleRateMatch = mimeType.match(/rate=(\d+)/);
                const sampleRate = sampleRateMatch ? parseInt(sampleRateMatch[1], 10) : 16000;
                const pcmData = base64ToArrayBuffer(audioData);
                const pcm16 = new Int16Array(pcmData);
                const wavBlob = pcmToWav(pcm16, sampleRate);
                const audioUrl = URL.createObjectURL(wavBlob);
                const audio = new Audio(audioUrl);
                audio.play();
            } else {
                console.error('Audio data or mime type not found in response');
            }
        } catch (error) {
            console.error('TTS API error:', error);
        } finally {
            readAloudBtn.textContent = 'Read Aloud';
            readAloudBtn.disabled = false;
        }
    }

    // Show the initial view on page load
    showView('role-selection-view');

    // Add event listeners for the new features
    document.getElementById('summarize-btn').addEventListener('click', () => {
        const medicalInput = document.getElementById('medical-input').value;
        if (medicalInput.trim()) {
            generateMedicalSummary(medicalInput);
        }
    });

    document.getElementById('read-aloud-btn').addEventListener('click', () => {
        const summaryText = document.getElementById('summary-text').textContent;
        if (summaryText.trim()) {
            generateTTS(summaryText);
        }
    });

// --- Queue Management Section: Backend Integration ---
let queueEntries = [];

function fetchAndRenderQueue() {
    fetch('http://localhost:8080/api/queue')
        .then(response => response.json())
        .then(data => {
            queueEntries = data || [];
            renderQueue(queueEntries);
        })
        .catch(error => {
            queueEntries = [];
            renderQueue(queueEntries);
            console.error('Error fetching queue:', error);
        });
}

function renderQueue(entries) {
    const list = document.getElementById('queue-list');
    if (!list) return;
    list.innerHTML = '';
    entries.forEach((q, i) => {
        const card = document.createElement('div');
        card.className = 'flex flex-col md:flex-row md:items-center md:justify-between p-4 rounded-xl shadow border border-slate-100 bg-gradient-to-r from-white to-slate-50 hover:shadow-lg transition';
        card.setAttribute('data-index', i);
        card.innerHTML = `
            <div class="flex flex-col md:flex-row md:items-center gap-2">
                <span class="font-bold text-sky-800 text-lg">${q.name || ''}</span>
                <span class="text-slate-500 text-sm">(${q.studentId || ''})</span>
                <span class="text-xs bg-slate-200 text-slate-700 px-2 py-1 rounded ml-0 md:ml-2">${q.bookedTime || ''}</span>
            </div>
            <div class="flex items-center gap-2 mt-2 md:mt-0">
                <span class="text-xs px-2 py-1 rounded-full ${q.status === 'Checked In' ? 'bg-sky-100 text-sky-600' : q.status === 'Waiting' ? 'bg-yellow-100 text-yellow-600' : q.status === 'In Progress' ? 'bg-green-100 text-green-600' : ''}">${q.status || ''}</span>
                <button class="queue-status-btn bg-yellow-500 text-white px-3 py-1 rounded-lg font-semibold hover:bg-yellow-600" data-index="${i}" data-status="Waiting">Mark Waiting</button>
                <button class="queue-status-btn bg-green-500 text-white px-3 py-1 rounded-lg font-semibold hover:bg-green-600" data-index="${i}" data-status="In Progress">Mark In Progress</button>
                <button class="queue-remove-btn bg-red-500 text-white px-3 py-1 rounded-lg font-semibold hover:bg-red-600" data-index="${i}">Remove</button>
            </div>
        `;
        list.appendChild(card);
    });
}

document.addEventListener('DOMContentLoaded', function () {
    // ...existing code...
    // --- Queue Management ---
    fetchAndRenderQueue();
    const queueSearchInput = document.getElementById('queue-search-input');
    if (queueSearchInput) {
        queueSearchInput.addEventListener('input', function(e) {
            const term = e.target.value.toLowerCase();
            const filtered = queueEntries.filter(q =>
                (q.name && q.name.toLowerCase().includes(term)) ||
                (q.studentId && q.studentId.toLowerCase().includes(term))
            );
            renderQueue(filtered);
        });
    }
    // Add to Queue
    const queueAddBtn = document.getElementById('queue-add-btn');
    const queueAddModal = document.getElementById('queue-add-modal');
    const queueAddCloseBtn = document.getElementById('queue-add-close-btn');
    const queueAddForm = document.getElementById('queue-add-form');
    if (queueAddBtn && queueAddModal && queueAddCloseBtn && queueAddForm) {
        queueAddBtn.addEventListener('click', function() {
            queueAddModal.classList.remove('hidden');
            queueAddForm.reset();
        });
        queueAddCloseBtn.addEventListener('click', function() {
            queueAddModal.classList.add('hidden');
        });
        queueAddForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const form = e.target;
            const newEntry = {
                name: form.name.value,
                studentId: form.studentId.value,
                bookedTime: form.bookedTime.value,
                status: 'Checked In'
            };
            fetch('http://localhost:8080/api/queue', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(newEntry)
            })
            .then(res => {
                if (!res.ok) throw new Error('Failed to add to queue');
                return res.json();
            })
            .then(() => {
                fetchAndRenderQueue();
                queueAddModal.classList.add('hidden');
                showQueueMsg('Added to queue!');
            })
            .catch(() => showQueueMsg('Failed to add to queue'));
        });
    }
    // Queue List Actions
    const queueList = document.getElementById('queue-list');
    if (queueList) {
        queueList.addEventListener('click', function(e) {
            const idx = e.target.dataset.index;
            const entry = queueEntries[idx];
            if (!entry) return;
            if (e.target.classList.contains('queue-status-btn')) {
                // Update status
                fetch(`http://localhost:8080/api/queue/${entry.id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ ...entry, status: e.target.dataset.status })
                })
                .then(res => res.json())
                .then(() => {
                    fetchAndRenderQueue();
                    showQueueMsg('Status updated!');
                })
                .catch(() => showQueueMsg('Failed to update status'));
            } else if (e.target.classList.contains('queue-remove-btn')) {
                if (confirm('Remove this patient from the queue?')) {
                    fetch(`http://localhost:8080/api/queue/${entry.id}`, {
                        method: 'DELETE'
                    })
                    .then(() => {
                        fetchAndRenderQueue();
                        showQueueMsg('Removed from queue');
                    })
                    .catch(() => showQueueMsg('Failed to remove from queue'));
                }
            }
        });
    }
    // Call Next
    const queueCallNextBtn = document.getElementById('queue-call-next-btn');
    if (queueCallNextBtn) {
        queueCallNextBtn.addEventListener('click', function() {
            if (!queueEntries.length) return showQueueMsg('Queue is empty!');
            const first = queueEntries[0];
            fetch(`http://localhost:8080/api/queue/${first.id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ ...first, status: 'In Progress' })
            })
            .then(res => res.json())
            .then(() => {
                fetchAndRenderQueue();
                showQueueMsg('Next patient called!');
            })
            .catch(() => showQueueMsg('Failed to call next'));
        });
    }
    // Mark Waiting
    const queueMarkWaitingBtn = document.getElementById('queue-mark-waiting-btn');
    if (queueMarkWaitingBtn) {
        queueMarkWaitingBtn.addEventListener('click', function() {
            if (!queueEntries.length) return showQueueMsg('No patient in queue!');
            const first = queueEntries[0];
            fetch(`http://localhost:8080/api/queue/${first.id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ ...first, status: 'Waiting' })
            })
            .then(res => res.json())
            .then(() => {
                fetchAndRenderQueue();
                showQueueMsg('Marked as Waiting');
            })
            .catch(() => showQueueMsg('Failed to mark as Waiting'));
        });
    }
    // Mark In Progress
    const queueMarkInProgressBtn = document.getElementById('queue-mark-inprogress-btn');
    if (queueMarkInProgressBtn) {
        queueMarkInProgressBtn.addEventListener('click', function() {
            if (!queueEntries.length) return showQueueMsg('No patient in queue!');
            const first = queueEntries[0];
            fetch(`http://localhost:8080/api/queue/${first.id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ ...first, status: 'In Progress' })
            })
            .then(res => res.json())
            .then(() => {
                fetchAndRenderQueue();
                showQueueMsg('Marked as In Progress');
            })
            .catch(() => showQueueMsg('Failed to mark as In Progress'));
        });
    }
});

function showQueueMsg(msg) {
    const el = document.getElementById('queue-action-msg');
    if (!el) return;
    el.textContent = msg;
    el.classList.remove('hidden');
    setTimeout(() => el.classList.add('hidden'), 1200);
}
// End of script.js
