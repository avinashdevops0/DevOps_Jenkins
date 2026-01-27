document.addEventListener('DOMContentLoaded', function() {
    const app = window.app || new MovieTicketsApp();
    const params = app.getUrlParams();
    
    let currentStep = 1;
    let selectedMovie = null;
    let selectedSeats = [];
    let seatPrice = 0;
    
    // DOM Elements
    const step1 = document.getElementById('step-1');
    const step2 = document.getElementById('step-2');
    const step3 = document.getElementById('step-3');
    const step4 = document.getElementById('step-4');
    const moviesList = document.getElementById('movies-list');
    const seatsContainer = document.getElementById('seats-container');
    const selectedSeatsCount = document.getElementById('selected-seats-count');
    const totalPriceElement = document.getElementById('total-price');
    const customerForm = document.getElementById('customer-form');
    const summaryDetails = document.getElementById('summary-details');
    const confirmBookingBtn = document.getElementById('confirm-booking');
    const confirmationDetails = document.getElementById('confirmation-details');
    
    // Initialize
    loadMovies();
    setupEventListeners();
    
    // Load movies for step 1
    function loadMovies() {
        fetch('/api/movies')
            .then(response => response.json())
            .then(movies => {
                moviesList.innerHTML = '';
                
                movies.forEach(movie => {
                    const movieItem = document.createElement('div');
                    movieItem.className = 'movie-selection-item';
                    movieItem.innerHTML = `
                        <div class="movie-selection-poster">
                            <div class="poster-placeholder">${movie.title.substring(0, 2)}</div>
                        </div>
                        <div class="movie-selection-info">
                            <h3>${movie.title}</h3>
                            <p class="movie-genre">${movie.genre} • ${movie.duration} min • ${movie.language}</p>
                            <div class="movie-rating">
                                <i class="fas fa-star"></i> ${movie.rating}
                            </div>
                            <p class="movie-description">${movie.description}</p>
                            <div class="movie-footer">
                                <p class="movie-price">$${movie.price}</p>
                                <button class="btn btn-primary select-movie" data-movie-id="${movie.id}">
                                    Select Movie
                                </button>
                            </div>
                        </div>
                    `;
                    moviesList.appendChild(movieItem);
                });
                
                // Add event listeners to select buttons
                document.querySelectorAll('.select-movie').forEach(button => {
                    button.addEventListener('click', function() {
                        const movieId = parseInt(this.dataset.movieId);
                        selectMovie(movieId);
                    });
                });
                
                // If movie ID is in URL params, auto-select it
                if (params.movie) {
                    const movieId = parseInt(params.movie);
                    selectMovie(movieId);
                }
            })
            .catch(error => {
                console.error('Error loading movies:', error);
                moviesList.innerHTML = `
                    <div class="error-message">
                        <i class="fas fa-exclamation-circle"></i>
                        <p>Failed to load movies. Please try again.</p>
                    </div>
                `;
            });
    }
    
    // Select a movie
    function selectMovie(movieId) {
        fetch(`/api/movies`)
            .then(response => response.json())
            .then(movies => {
                selectedMovie = movies.find(m => m.id === movieId);
                if (selectedMovie) {
                    seatPrice = selectedMovie.price;
                    
                    // Update UI
                    document.querySelectorAll('.movie-selection-item').forEach(item => {
                        item.classList.remove('selected');
                    });
                    
                    const selectedItem = document.querySelector(`[data-movie-id="${movieId}"]`).closest('.movie-selection-item');
                    selectedItem.classList.add('selected');
                    
                    app.showNotification(`${selectedMovie.title} selected!`, 'success');
                }
            });
    }
    
    // Load seats for step 2
    function loadSeats() {
        if (!selectedMovie) {
            seatsContainer.innerHTML = '<div class="loading">Please select a movie first</div>';
            return;
        }
        
        fetch(`/api/seats/${selectedMovie.id}`)
            .then(response => response.json())
            .then(seats => {
                seatsContainer.innerHTML = '';
                selectedSeats = [];
                updateSeatSelection();
                
                seats.forEach(seat => {
                    const seatElement = document.createElement('div');
                    seatElement.className = `seat ${seat.available ? 'available' : 'booked'}`;
                    seatElement.textContent = seat.id;
                    seatElement.dataset.seatId = seat.id;
                    
                    if (seat.available) {
                        seatElement.addEventListener('click', () => toggleSeat(seat.id));
                    }
                    
                    seatsContainer.appendChild(seatElement);
                });
            })
            .catch(error => {
                console.error('Error loading seats:', error);
                seatsContainer.innerHTML = `
                    <div class="error-message">
                        <i class="fas fa-exclamation-circle"></i>
                        <p>Failed to load seats. Please try again.</p>
                    </div>
                `;
            });
    }
    
    // Toggle seat selection
    function toggleSeat(seatId) {
        const index = selectedSeats.indexOf(seatId);
        const seatElement = document.querySelector(`[data-seat-id="${seatId}"]`);
        
        if (index === -1) {
            selectedSeats.push(seatId);
            seatElement.classList.remove('available');
            seatElement.classList.add('selected');
        } else {
            selectedSeats.splice(index, 1);
            seatElement.classList.remove('selected');
            seatElement.classList.add('available');
        }
        
        updateSeatSelection();
    }
    
    // Update seat selection display
    function updateSeatSelection() {
        selectedSeatsCount.textContent = selectedSeats.length;
        const totalPrice = selectedSeats.length * seatPrice;
        totalPriceElement.textContent = totalPrice.toFixed(2);
    }
    
    // Update booking summary
    function updateSummary() {
        if (!selectedMovie || selectedSeats.length === 0) return;
        
        const totalPrice = selectedSeats.length * seatPrice;
        
        summaryDetails.innerHTML = `
            <div class="summary-item">
                <span>Movie:</span>
                <span>${selectedMovie.title}</span>
            </div>
            <div class="summary-item">
                <span>Seats:</span>
                <span>${selectedSeats.map(s => 'A' + s).join(', ')}</span>
            </div>
            <div class="summary-item">
                <span>Seats Count:</span>
                <span>${selectedSeats.length}</span>
            </div>
            <div class="summary-item">
                <span>Price per Seat:</span>
                <span>$${seatPrice.toFixed(2)}</span>
            </div>
            <div class="summary-item">
                <span>Total Amount:</span>
                <span>$${totalPrice.toFixed(2)}</span>
            </div>
        `;
    }
    
    // Handle step navigation
    function goToStep(stepNumber) {
        // Hide all steps
        document.querySelectorAll('.booking-step').forEach(step => {
            step.classList.remove('active');
        });
        
        // Show target step
        document.getElementById(`step-${stepNumber}`).classList.add('active');
        currentStep = stepNumber;
        
        // Load data for step if needed
        if (stepNumber === 2) {
            loadSeats();
        } else if (stepNumber === 3) {
            updateSummary();
        }
    }
    
    // Validate form
    function validateForm() {
        const name = document.getElementById('customer-name').value.trim();
        const email = document.getElementById('customer-email').value.trim();
        const phone = document.getElementById('customer-phone').value.trim();
        
        if (!name || !email || !phone) {
            app.showNotification('Please fill all required fields', 'error');
            return false;
        }
        
        if (!validateEmail(email)) {
            app.showNotification('Please enter a valid email address', 'error');
            return false;
        }
        
        if (!validatePhone(phone)) {
            app.showNotification('Please enter a valid phone number', 'error');
            return false;
        }
        
        return true;
    }
    
    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }
    
    function validatePhone(phone) {
        const re = /^[\+]?[1-9][\d]{0,15}$/;
        return re.test(phone.replace(/[^\d+]/g, ''));
    }
    
    // Submit booking
    function submitBooking() {
        if (!validateForm()) return;
        
        if (!selectedMovie || selectedSeats.length === 0) {
            app.showNotification('Please select seats first', 'error');
            goToStep(2);
            return;
        }
        
        const bookingData = {
            movieId: selectedMovie.id,
            seatNumbers: selectedSeats,
            customerName: document.getElementById('customer-name').value.trim(),
            customerEmail: document.getElementById('customer-email').value.trim(),
            customerPhone: document.getElementById('customer-phone').value.trim()
        };
        
        confirmBookingBtn.disabled = true;
        confirmBookingBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';
        
        // Submit booking
        fetch('/api/bookings', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(bookingData)
        })
        .then(response => response.json())
        .then(booking => {
            // Show confirmation
            showConfirmation(booking);
            goToStep(4);
        })
        .catch(error => {
            console.error('Booking error:', error);
            app.showNotification('Booking failed. Please try again.', 'error');
        })
        .finally(() => {
            confirmBookingBtn.disabled = false;
            confirmBookingBtn.innerHTML = 'Confirm Booking <i class="fas fa-check"></i>';
        });
    }
    
    // Show confirmation details
    function showConfirmation(booking) {
        confirmationDetails.innerHTML = `
            <div class="confirmation-item">
                <span>Booking ID:</span>
                <strong>${booking.bookingId}</strong>
            </div>
            <div class="confirmation-item">
                <span>Movie:</span>
                <span>${selectedMovie.title}</span>
            </div>
            <div class="confirmation-item">
                <span>Seats:</span>
                <span>${booking.seatNumbers.map(s => 'A' + s).join(', ')}</span>
            </div>
            <div class="confirmation-item">
                <span>Customer:</span>
                <span>${booking.customerName}</span>
            </div>
            <div class="confirmation-item">
                <span>Email:</span>
                <span>${booking.customerEmail}</span>
            </div>
            <div class="confirmation-item">
                <span>Total Amount:</span>
                <strong>$${booking.totalAmount.toFixed(2)}</strong>
            </div>
            <div class="confirmation-item">
                <span>Status:</span>
                <span class="status-confirmed">${booking.status}</span>
            </div>
        `;
    }
    
    // Setup event listeners
    function setupEventListeners() {
        // Next step buttons
        document.querySelectorAll('.next-step').forEach(button => {
            button.addEventListener('click', function() {
                const nextStep = parseInt(this.dataset.next.split('-')[1]);
                
                // Validate current step
                if (currentStep === 1 && !selectedMovie) {
                    app.showNotification('Please select a movie first', 'error');
                    return;
                }
                
                if (currentStep === 2 && selectedSeats.length === 0) {
                    app.showNotification('Please select at least one seat', 'error');
                    return;
                }
                
                goToStep(nextStep);
            });
        });
        
        // Previous step buttons
        document.querySelectorAll('.prev-step').forEach(button => {
            button.addEventListener('click', function() {
                const prevStep = parseInt(this.dataset.prev.split('-')[1]);
                goToStep(prevStep);
            });
        });
        
        // Confirm booking button
        confirmBookingBtn.addEventListener('click', submitBooking);
        
        // Form submission prevention
        customerForm.addEventListener('submit', function(e) {
            e.preventDefault();
            submitBooking();
        });
    }
});