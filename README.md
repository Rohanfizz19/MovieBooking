# Movie Ticket Booking API

A REST API for booking movie tickets, built with Spring Boot 3.2, Java 17, MySQL, and JWT authentication.

## Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8 running on localhost:3306 (for production)

### Database
The app auto-creates the `movie_booking` database. Default MySQL creds in `application.yml` are root/root -- change as needed.

### Run
```bash
mvn spring-boot:run
```

The app starts on port 8080. A default admin user is seeded on first run:
- Email: `admin@moviebooking.com`
- Password: `admin123`

## Architecture

- **Auth**: Stateless JWT. Roles: ADMIN, CUSTOMER.
- **Booking flow**: Hold seats (pessimistic lock) -> Confirm (pricing + discount + payment) -> Cancel (refund policy applied)
- **Schedulers**: Expired hold cleanup (60s), reminder notifications (5 min)

### Package layout
```
org.bms.movieticketbooking
├── config/          Security, JWT, DataInitializer
├── controller/      Auth, Admin, Browse, Booking
├── dto/             request/ and response/
├── entity/
│   ├── infra/       City, Theatre, Screen, Seat
│   ├── content/     Movie, Show, ShowSeat
│   ├── transactions/ Booking, BookingItem
│   └── supporting/  User, PricingRule, DiscountCode, RefundPolicy, Payment, Notification
├── enums/
├── exception/
├── repository/
├── scheduler/
└── service/
```

## Core APIs

### Auth
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | /api/auth/register | Public |
| POST | /api/auth/login | Public |

### Admin
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | /api/admin/cities | ADMIN |
| POST | /api/admin/theatres | ADMIN |
| POST | /api/admin/screens | ADMIN |
| POST | /api/admin/movies | ADMIN |
| POST | /api/admin/shows | ADMIN |
| POST | /api/admin/pricing-rules | ADMIN |
| POST | /api/admin/discount-codes | ADMIN |
| POST | /api/admin/refund-policies | ADMIN |

### Browse
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | /api/cities | Authenticated |
| GET | /api/cities/{cityId}/movies | Authenticated |
| GET | /api/movies/{movieId}/shows?cityId= | Authenticated |
| GET | /api/shows/{showId}/seats | Authenticated |

### Booking
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | /api/bookings/hold | CUSTOMER |
| POST | /api/bookings/confirm | CUSTOMER |
| POST | /api/bookings/{id}/cancel | CUSTOMER |
| GET | /api/bookings | CUSTOMER |

## Running Tests

Tests use an in-memory H2 database (profile: test).

```bash
mvn test                                    # all tests
mvn test -Dtest=AuthServiceTest             # single class
mvn test -Dtest=BookingFlowIntegrationTest  # end-to-end flow
```

## Configuration

Key properties in `application.yml`:
- `app.jwt.secret` -- signing key for JWT tokens
- `app.jwt.expiration-ms` -- token lifetime (default 24h)
- `app.booking.hold-timeout-minutes` -- how long held seats stay reserved (default 10 min)
