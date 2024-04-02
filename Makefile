build-image:
	docker build --no-cache -t compass .
run:
	docker compose up
stop:
	docker compose down