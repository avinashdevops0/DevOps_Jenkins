document.addEventListener('DOMContentLoaded', function() {
    const bookingsList = document.getElementById('bookings-list');
    const noBookings = document.getElementById('no-bookings');
    const refreshBtn = document.getElementById('refresh-bookings');
    
    // Load bookings
    function loadBookings() {
        bookingsList.innerHTML = '<div class="loading">Loading bookings...</div>';
        
        fetch('/api/bookings')
            .then(response => response.json())
            .then(bookings => {
                if (bookings.length === 0) {
                    bookingsList.style.display = 'none';
                    noBookings.style.display = 'block';
                    return;
                }
                
                bookingsList.style.display = 'block';
                noBookings.style.display = 'none';
                bookingsList.innerHTML = '';
                
                // Get movies data for display
                fetch('/api/movies')
                    .then(response => response.json())
                    .then(movies => {
                        displayBookings(bookings, movies);
                    })
                    .catch(error => {
                        console.error('Error loading movies:', error);
                        displayBookings(bookings, []);
                    });
            })
            .catch(error => {
                console.error('Error loading bookings:', error);
                bookingsList.innerHTML = `
                    <div class="error-message">
                        <i class="fas fa-exclamation-circle"></i>
                        <p>Failed to load bookings. Please try again.</p>
                    </div>
                `;
            });
    }
    
    // Display bookings
    function displayBookings(bookings, movies) {
        bookings.forEach(booking => {
            const movie = movies.find(m => m.id === booking.movieId);
            const bookingDate = new Date(booking.bookingDate).toLocaleDateString('en-US', {
                weekday: 'long',
                year: 'numeric',
                month: 'long',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });
            
            const bookingCard = document.createElement('div');
            bookingCard.className = 'booking-card';
            bookingCard.innerHTML = `
                <div class="booking-header">
                    <div class="booking-id">${booking.bookingId}</div>
                    <div class="booking-status status-${booking.status.toLowerCase()}">
                        ${booking.status}
                    </div>
                </div>
                
                <div class="booking-details">
                    <div class="detail-item">
                        <div class="detail-label">Movie</div>
                        <div class="detail-value">${movie ? movie.title : 'Unknown Movie'}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Seats</div>
                        <div class="detail-value">${booking.seatNumbers.map(s => 'A' + s).join(', ')}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Customer</div>
                        <div class="detail-value">${booking.customerName}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Booking Date</div>
                        <div class="detail-value">${bookingDate}</div>
                    </div>
                    <div class="detail-item">
                        <div class="detail-label">Total Amount</div>
                        <div class="detail-value">$${booking.totalAmount.toFixed(2)}</div>
                    </div>
                </div>
                
                <div class="booking-actions">
                    <button class="btn btn-small btn-secondary print-ticket" data-booking-id="${booking.bookingId}">
                        <i class="fas fa-print"></i> Print Ticket
                    </button>
                </div>
            `;
            
            bookingsList.appendChild(bookingCard);
        });
        
        // Add event listeners to print buttons
        document.querySelectorAll('.print-ticket').forEach(button => {
            button.addEventListener('click', function() {
                const bookingId = this.dataset.bookingId;
                printTicket(bookingId);
            });
        });
    }
    
    // Print ticket
    function printTicket(bookingId) {
        fetch('/api/bookings')
            .then(response => response.json())
            .then(bookings => {
                const booking = bookings.find(b => b.bookingId === bookingId);
                if (booking) {
                    fetch('/api/movies')
                        .then(response => response.json())
                        .then(movies => {
                            const movie = movies.find(m => m.id === booking.movieId);
                            const printWindow = window.open('', '_blank');
                            
                            printWindow.document.write(`
                                <!DOCTYPE html>
                                <html>
                                <head>
                                    <title>Ticket - ${booking.bookingId}</title>
                                    <style>
                                        body { font-family: Arial, sans-serif; padding: 20px; }
                                        .ticket { border: 2px dashed #333; padding: 20px; max-width: 400px; margin: 0 auto; }
                                        .header { text-align: center; border-bottom: 2px solid #333; padding-bottom: 10px; margin-bottom: 20px; }
                                        .movie-title { font-size: 24px; font-weight: bold; margin: 10px 0; }
                                        .details { margin: 20px 0; }
                                        .detail-item { display: flex; justify-content: space-between; margin: 5px 0; }
                                        .barcode { text-align: center; margin: 20px 0; font-family: monospace; }
                                        .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #666; }
                                        @media print { .no-print { display: none; } }
                                    </style>
                                </head>
                                <body>
                                    <div class="ticket">
                                        <div class="header">
                                            <h1>MOVIE TICKETS</h1>
                                            <p>Your Ticket to Entertainment</p>
                                        </div>
                                        
                                        <div class="movie-title">${movie ? movie.title : 'Movie'}</div>
                                        
                                        <div class="details">
                                            <div class="detail-item">
                                                <span>Booking ID:</span>
                                                <span>${booking.bookingId}</span>
                                            </div>
                                            <div class="detail-item">
                                                <span>Seats:</span>
                                                <span>${booking.seatNumbers.map(s => 'A' + s).join(', ')}</span>
                                            </div>
                                            <div class="detail-item">
                                                <span>Customer:</span>
                                                <span>${booking.customerName}</span>
                                            </div>
                                            <div class="detail-item">
                                                <span>Date:</span>
                                                <span>${new Date(booking.bookingDate).toLocaleDateString()}</span>
                                            </div>
                                            <div class="detail-item">
                                                <span>Total:</span>
                                                <span>$${booking.totalAmount.toFixed(2)}</span>
                                            </div>
                                        </div>
                                        
                                        <div class="barcode">
                                            <div>${booking.bookingId}</div>
                                            <div style="letter-spacing: 5px; font-size: 24px;">|||| |||| |||| ||||</div>
                                        </div>
                                        
                                        <div class="footer">
                                            <p>Thank you for booking with us!</p>
                                            <p>Please arrive 15 minutes before showtime</p>
                                        </div>
                                    </div>
                                    
                                    <div class="no-print" style="text-align: center; margin-top: 20px;">
                                        <button onclick="window.print()">Print Ticket</button>
                                        <button onclick="window.close()">Close</button>
                                    </div>
                                </body>
                                </html>
                            `);
                            
                            printWindow.document.close();
                        });
                }
            });
    }
    
    // Event listeners
    refreshBtn.addEventListener('click', loadBookings);
    
    // Load bookings on page load
    loadBookings();
});