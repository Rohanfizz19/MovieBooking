
# 1. Login as admin
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
-H "Content-Type: application/json" \
-d '{"email":"admin@moviebooking.com","password":"admin123"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['token'])")

echo "Admin token: $ADMIN_TOKEN"

# 2. Create city
curl -s -X POST http://localhost:8080/api/admin/cities \
-H "Authorization: Bearer $ADMIN_TOKEN" \
-H "Content-Type: application/json" \
-d '{"name":"Mumbai"}'

# 3. Create theatre
curl -s -X POST http://localhost:8080/api/admin/theatres \
-H "Authorization: Bearer $ADMIN_TOKEN" \
-H "Content-Type: application/json" \
-d '{"name":"PVR Phoenix","address":"Lower Parel","cityId":1}'

# 4. Create screen with seats
curl -s -X POST http://localhost:8080/api/admin/screens \
-H "Authorization: Bearer $ADMIN_TOKEN" \
-H "Content-Type: application/json" \
-d '{"name":"Screen 1","theatreId":1,"seatLayout":[{"rowLabel":"A","seatCount":5,"seatType":"REGULAR"},{"rowLabel":"B","seatCount":5,"seatType":"PREMIUM"}]}'

# 5. Create movie
curl -s -X POST http://localhost:8080/api/admin/movies \
-H "Authorization: Bearer $ADMIN_TOKEN" \
-H "Content-Type: application/json" \
-d '{"title":"Inception","description":"Dream within a dream","durationMinutes":148,"language":"English","genre":"Sci-Fi"}'

# 6. Create show (use a future time)
curl -s -X POST http://localhost:8080/api/admin/shows \
-H "Authorization: Bearer $ADMIN_TOKEN" \
-H "Content-Type: application/json" \
-d '{"movieId":1,"screenId":1,"startTime":"2026-07-10T19:00:00","pricingTier":"REGULAR"}'

# 7. Create pricing rule
curl -s -X POST http://localhost:8080/api/admin/pricing-rules \
-H "Authorization: Bearer $ADMIN_TOKEN" \
-H "Content-Type: application/json" \
-d '{"seatType":"REGULAR","pricingTier":"REGULAR","price":250}'

curl -s -X POST http://localhost:8080/api/admin/pricing-rules \
-H "Authorization: Bearer $ADMIN_TOKEN" \
-H "Content-Type: application/json" \
-d '{"seatType":"PREMIUM","pricingTier":"REGULAR","price":450}'

# 8. Register customer
CUST_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{"email":"rohan@test.com","password":"pass123","name":"Rohan"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['token'])")

echo "Customer token: $CUST_TOKEN"

# 9. Browse — see available seats
curl -s http://localhost:8080/api/shows/1/seats \
-H "Authorization: Bearer $CUST_TOKEN" | python3 -m json.tool

# 10. Hold seats (pick showSeatIds 1 and 2 = seats A1, A2)
curl -s -X POST http://localhost:8080/api/bookings/hold \
-H "Authorization: Bearer $CUST_TOKEN" \
-H "Content-Type: application/json" \
-d '{"showId":1,"showSeatIds":[1,2]}'

# 11. Confirm booking
curl -s -X POST http://localhost:8080/api/bookings/confirm \
-H "Authorization: Bearer $CUST_TOKEN" \
-H "Content-Type: application/json" \
-d '{"showId":1,"showSeatIds":[1,2],"paymentMethod":"CREDIT_CARD"}' | python3 -m json.tool

# 12. Check booking history
curl -s http://localhost:8080/api/bookings \
-H "Authorization: Bearer $CUST_TOKEN" | python3 -m json.tool

---

# Sad Flow — Double booking (seat already held by another user)

# Register a second customer
CUST2_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/register \
-H "Content-Type: application/json" \
-d '{"email":"amit@test.com","password":"pass123","name":"Amit"}' | python3 -c "import sys,json; print(json.load(sys.stdin)['data']['token'])")

echo "Customer 2 token: $CUST2_TOKEN"

# Customer 2 tries to hold the SAME seats (1,2) that Customer 1 already booked
# This will fail with "One or more seats are not available"
curl -s -X POST http://localhost:8080/api/bookings/hold \
-H "Authorization: Bearer $CUST2_TOKEN" \
-H "Content-Type: application/json" \
-d '{"showId":1,"showSeatIds":[1,2]}' | python3 -m json.tool

# Expected response:
# { "success": false, "message": "One or more seats are not available", "data": null }

# Customer 2 books different seats (3,4) — this works
curl -s -X POST http://localhost:8080/api/bookings/hold \
-H "Authorization: Bearer $CUST2_TOKEN" \
-H "Content-Type: application/json" \
-d '{"showId":1,"showSeatIds":[3,4]}'

# Customer 2 confirms
curl -s -X POST http://localhost:8080/api/bookings/confirm \
-H "Authorization: Bearer $CUST2_TOKEN" \
-H "Content-Type: application/json" \
-d '{"showId":1,"showSeatIds":[3,4],"paymentMethod":"UPI"}' | python3 -m json.tool
