document.addEventListener('DOMContentLoaded', function() {
    const moviesContainer = document.getElementById('movies-container');
    const genreFilter = document.getElementById('genreFilter');
    const sortFilter = document.getElementById('sortFilter');
    
    let allMovies = [];
    
    // Load movies
    function loadMovies() {
        fetch('/api/movies')
            .then(response => response.json())
            .then(movies => {
                allMovies = movies;
                displayMovies(movies);
            })
            .catch(error => {
                console.error('Error loading movies:', error);
                moviesContainer.innerHTML = `
                    <div class="error-message">
                        <i class="fas fa-exclamation-circle"></i>
                        <p>Failed to load movies. Please try again later.</p>
                    </div>
                `;
            });
    }
    
    // Display movies with filtering and sorting
    function displayMovies(movies) {
        if (movies.length === 0) {
            moviesContainer.innerHTML = `
                <div class="no-movies">
                    <i class="fas fa-film"></i>
                    <h3>No Movies Available</h3>
                    <p>Check back later for new releases!</p>
                </div>
            `;
            return;
        }
        
        moviesContainer.innerHTML = '';
        
        movies.forEach(movie => {
            const movieCard = document.createElement('div');
            movieCard.className = 'movie-card';
            movieCard.innerHTML = `
                <div class="movie-poster">
                    <div class="poster-placeholder">${movie.title.substring(0, 2)}</div>
                </div>
                <div class="movie-info">
                    <h3>${movie.title}</h3>
                    <p class="movie-genre">${movie.genre}</p>
                    <div class="movie-details">
                        <div class="movie-detail">
                            <i class="fas fa-clock"></i>
                            <span>${movie.duration} min</span>
                        </div>
                        <div class="movie-detail">
                            <i class="fas fa-language"></i>
                            <span>${movie.language}</span>
                        </div>
                        <div class="movie-detail">
                            <i class="fas fa-certificate"></i>
                            <span>${movie.certificate}</span>
                        </div>
                    </div>
                    <div class="movie-rating">
                        <i class="fas fa-star"></i> ${movie.rating}
                    </div>
                    <p class="movie-description">${movie.description}</p>
                    <div class="movie-footer">
                        <p class="movie-price">$${movie.price.toFixed(2)}</p>
                        <a href="booking.html?movie=${movie.id}" class="btn btn-primary btn-small">
                            <i class="fas fa-ticket-alt"></i> Book Now
                        </a>
                    </div>
                </div>
            `;
            moviesContainer.appendChild(movieCard);
        });
    }
    
    // Apply filters
    function applyFilters() {
        let filteredMovies = [...allMovies];
        
        // Apply genre filter
        const selectedGenre = genreFilter.value;
        if (selectedGenre) {
            filteredMovies = filteredMovies.filter(movie => 
                movie.genre.includes(selectedGenre)
            );
        }
        
        // Apply sort
        const sortBy = sortFilter.value;
        switch(sortBy) {
            case 'rating':
                filteredMovies.sort((a, b) => b.rating - a.rating);
                break;
            case 'price_low':
                filteredMovies.sort((a, b) => a.price - b.price);
                break;
            case 'price_high':
                filteredMovies.sort((a, b) => b.price - a.price);
                break;
        }
        
        displayMovies(filteredMovies);
    }
    
    // Event listeners
    genreFilter.addEventListener('change', applyFilters);
    sortFilter.addEventListener('change', applyFilters);
    
    // Load movies on page load
    loadMovies();
});