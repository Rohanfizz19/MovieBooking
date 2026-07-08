# CLAUDE.md

## What is this?
Built this as a take-home assignment reference — movie ticket booking REST API. Started with just "build a booking system with seat locking" and iterated from there. The requirements came in pieces, not as one big spec.

## How it evolved
- Started with the entity model and auth (JWT + Spring Security)
- Then added the admin setup APIs (city/theatre/screen/movie/show)
- Booking flow came next — hold seats with pessimistic lock, then confirm with pricing
- Discount codes and refund policies were added later when the scope expanded
- Notifications and schedulers were the last piece

## Build & Run
```bash
mvn spring-boot:run    # starts on 8080, uses H2 in-memory
mvn test               # 15 tests, all passing
```

No MySQL needed — runs on H2 by default. Data resets on restart.
Default admin: `admin@moviebooking.com` / `admin123`

## Project layout
```
org.bms.movieticketbooking
├── config/        JWT filter, security config, data seeder
├── controller/    Auth, Admin, Browse, Booking
├── dto/           request + response objects
├── entity/        infra (City/Theatre/Screen/Seat), content (Movie/Show/ShowSeat), transactions (Booking/BookingItem), supporting (User/Payment/etc)
├── enums/         Role, SeatType, SeatStatus, BookingStatus, PricingTier, PaymentStatus
├── exception/     GlobalExceptionHandler + custom exceptions
├── repository/    JPA repos, ShowSeatRepository has the pessimistic lock query
├── scheduler/     hold cleanup (60s) + reminder sender (5min)
└── service/       business logic lives here
```

## Key decisions made along the way
- **ShowSeat as separate entity** — physical seat exists once, availability is per-show. Came up when thinking about "how does the same seat A5 show as booked for 7pm but available for 9pm?"
- **Pessimistic locking for holds** — tried optimistic first, race conditions were nasty. SELECT FOR UPDATE solved it.
- **Pricing as a lookup table** — didn't hardcode prices. PricingRule maps (seatType + pricingTier) → price. Easy to extend without code changes.
- **Sync notifications** — tried @Async initially but hit FK constraint issues (notification saved before booking committed). Went synchronous, works fine for this scale.
- **H2 for dev** — MySQL was the original plan but password config issues made H2 the path of least resistance. Production would swap back to MySQL.

## Testing
- Unit tests mock repositories, test business logic (hold/confirm/cancel/discount)
- Integration tests spin up full context with H2, test the actual HTTP flow end-to-end
- `testing.md` has the curl commands for manual testing

## Gotchas I hit
- ByteBuddy version conflicts with Java 23 — needed `<byte-buddy.version>1.14.19</byte-buddy.version>` in properties
- Hibernate lazy proxy serialization — controllers return Map instead of raw entities
- Mockito on Java 23 needs `mock-maker-subclass` extension file + surefire `--add-opens` args
