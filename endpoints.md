# Ascender API Documentation

This document provides a comprehensive list of all endpoints available in the Ascender API.

## Table of Contents

- [Information](#information)
  - [Endpoints List](#endpoints-list)
- [Games](#games)
  - [Get All Games](#get-all-games)
  - [Get Game by ID](#get-game-by-id)
  - [Create Game](#create-game)
  - [Update Game](#update-game)
  - [Delete Game](#delete-game)
  - [Get Scrimable Games](#get-scrimable-games)
- [Player Profiles](#player-profiles)
  - [Get All Player Profiles](#get-all-player-profiles)
  - [Get Player Profile by ID](#get-player-profile-by-id)
  - [Create Player Profile](#create-player-profile)
  - [Update Player Profile](#update-player-profile)
  - [Delete Player Profile](#delete-player-profile)
  - [Update Profile Avatar](#update-profile-avatar)
  - [Update Profile Background](#update-profile-background)
  - [Find Player Profiles by Skill Level](#find-player-profiles-by-skill-level)
  - [Find Player Profiles Looking for Team](#find-player-profiles-looking-for-team)
- [Scrims](#scrims)
  - [Get All Scrims](#get-all-scrims)
  - [Get Scrim by ID](#get-scrim-by-id)
  - [Create Scrim](#create-scrim)
  - [Update Scrim](#update-scrim)
  - [Delete Scrim](#delete-scrim)
  - [Get All Scrim Requests](#get-all-scrim-requests)
  - [Get Scrim Request by ID](#get-scrim-request-by-id)
  - [Create Scrim Request](#create-scrim-request)
  - [Accept Scrim Request](#accept-scrim-request)
  - [Add Player to Scrim](#add-player-to-scrim)
  - [Complete Scrim](#complete-scrim)
  - [Find Active Scrims by Game](#find-active-scrims-by-game)
  - [Find Pending Scrim Requests by Game](#find-pending-scrim-requests-by-game)
- [Teams](#teams)
  - [Get All Teams](#get-all-teams)
  - [Get Team by ID](#get-team-by-id)
  - [Create Team](#create-team)
  - [Update Team](#update-team)
  - [Delete Team](#delete-team)
  - [Add Player to Team](#add-player-to-team)
  - [Remove Player from Team](#remove-player-from-team)
  - [Find Teams by Game](#find-teams-by-game)
  - [Find Available Teammates by Game](#find-available-teammates-by-game)

## Information

### Endpoints List

**GET /api/public/info/endpoints**

Returns a list of all endpoints available in the application.

**Response:** List of strings representing all available endpoints

## Games

### Get All Games

**GET /api/games**

Returns a list of all games.

**Response:** List of Game objects

### Get Game by ID

**GET /api/games/{id}**

Returns a specific game by ID.

**Path Parameters:**
- `id` - UUID of the game

**Response:**
- 200 OK - Game object
- 404 Not Found - If game with specified ID doesn't exist

### Create Game

**POST /api/games**

Creates a new game.

**Request Body:** Game object

**Response:**
- 201 Created - Created Game object

### Update Game

**PUT /api/games/{id}**

Updates an existing game.

**Path Parameters:**
- `id` - UUID of the game to update

**Request Body:** Game object

**Response:**
- 200 OK - Updated Game object
- 404 Not Found - If game with specified ID doesn't exist

### Delete Game

**DELETE /api/games/{id}**

Deletes a game.

**Path Parameters:**
- `id` - UUID of the game to delete

**Response:**
- 204 No Content - If deletion was successful
- 404 Not Found - If game with specified ID doesn't exist

### Get Scrimable Games

**GET /api/games/scrimable**

Returns a list of games that support scrims.

**Response:** List of Game objects that support scrims

## Player Profiles

### Get All Player Profiles

**GET /api/players**

Returns a list of all player profiles.

**Response:** List of PlayerProfile objects

### Get Player Profile by ID

**GET /api/players/{id}**

Returns a specific player profile by ID.

**Path Parameters:**
- `id` - UUID of the player profile

**Response:**
- 200 OK - PlayerProfile object
- 404 Not Found - If player profile with specified ID doesn't exist

### Create Player Profile

**POST /api/players**

Creates a new player profile.

**Request Body:** PlayerProfile object

**Response:**
- 201 Created - Created PlayerProfile object

### Update Player Profile

**PUT /api/players/{id}**

Updates an existing player profile.

**Path Parameters:**
- `id` - UUID of the player profile to update

**Request Body:** PlayerProfile object

**Response:**
- 200 OK - Updated PlayerProfile object
- 404 Not Found - If player profile with specified ID doesn't exist

### Delete Player Profile

**DELETE /api/players/{id}**

Deletes a player profile.

**Path Parameters:**
- `id` - UUID of the player profile to delete

**Response:**
- 204 No Content - If deletion was successful
- 404 Not Found - If player profile with specified ID doesn't exist

### Update Profile Avatar

**POST /api/players/{id}/avatar**

Updates a player's avatar image.

**Path Parameters:**
- `id` - UUID of the player profile

**Request Body:** Multipart form data with a file field named "file"

**Response:**
- 200 OK - Updated PlayerProfile object
- 404 Not Found - If player profile with specified ID doesn't exist
- 500 Internal Server Error - If there was an error processing the file

### Update Profile Background

**POST /api/players/{id}/background**

Updates a player's profile background image.

**Path Parameters:**
- `id` - UUID of the player profile

**Request Body:** Multipart form data with a file field named "file"

**Response:**
- 200 OK - Updated PlayerProfile object
- 404 Not Found - If player profile with specified ID doesn't exist
- 500 Internal Server Error - If there was an error processing the file

### Find Player Profiles by Skill Level

**GET /api/players/skill/{skillLevel}**

Returns player profiles with a specific skill level.

**Path Parameters:**
- `skillLevel` - Skill level to search for

**Response:** List of PlayerProfile objects with the specified skill level

### Find Player Profiles Looking for Team

**GET /api/players/looking-for-team**

Returns player profiles that are looking for a team.

**Response:** List of PlayerProfile objects that are looking for a team

## Scrims

### Get All Scrims

**GET /api/scrims**

Returns a list of all scrims.

**Response:** List of Scrim objects

### Get Scrim by ID

**GET /api/scrims/{id}**

Returns a specific scrim by ID.

**Path Parameters:**
- `id` - UUID of the scrim

**Response:**
- 200 OK - Scrim object
- 404 Not Found - If scrim with specified ID doesn't exist

### Create Scrim

**POST /api/scrims**

Creates a new scrim.

**Request Body:** Scrim object

**Response:**
- 201 Created - Created Scrim object

### Update Scrim

**PUT /api/scrims/{id}**

Updates an existing scrim.

**Path Parameters:**
- `id` - UUID of the scrim to update

**Request Body:** Scrim object

**Response:**
- 200 OK - Updated Scrim object
- 404 Not Found - If scrim with specified ID doesn't exist

### Delete Scrim

**DELETE /api/scrims/{id}**

Deletes a scrim.

**Path Parameters:**
- `id` - UUID of the scrim to delete

**Response:**
- 204 No Content - If deletion was successful
- 404 Not Found - If scrim with specified ID doesn't exist

### Get All Scrim Requests

**GET /api/scrims/requests**

Returns a list of all scrim requests.

**Response:** List of ScrimRequest objects

### Get Scrim Request by ID

**GET /api/scrims/requests/{id}**

Returns a specific scrim request by ID.

**Path Parameters:**
- `id` - UUID of the scrim request

**Response:**
- 200 OK - ScrimRequest object
- 404 Not Found - If scrim request with specified ID doesn't exist

### Create Scrim Request

**POST /api/scrims/requests**

Creates a new scrim request.

**Request Body:** JSON object with the following fields:
- `name` - Name of the scrim request
- `description` - Description of the scrim request
- `gameId` - UUID of the game as a string
- `teamId` - UUID of the requesting team as a string

**Response:**
- 201 Created - Created ScrimRequest object
- 400 Bad Request - If the request data is invalid

### Accept Scrim Request

**POST /api/scrims/requests/{id}/accept**

Accepts a scrim request and creates a scrim.

**Path Parameters:**
- `id` - UUID of the scrim request to accept

**Request Body:** JSON object with the following fields:
- `acceptingTeamId` - UUID of the accepting team as a string

**Response:**
- 201 Created - Created Scrim object
- 400 Bad Request - If the request data is invalid or the scrim request cannot be accepted

### Add Player to Scrim

**POST /api/scrims/{id}/players**

Adds a player to a scrim.

**Path Parameters:**
- `id` - UUID of the scrim

**Request Body:** JSON object with the following fields:
- `playerId` - UUID of the player to add as a string

**Response:**
- 200 OK - Updated Scrim object
- 400 Bad Request - If the request data is invalid or the player cannot be added

### Complete Scrim

**POST /api/scrims/{id}/complete**

Marks a scrim as completed and records the result.

**Path Parameters:**
- `id` - UUID of the scrim to complete

**Request Body:** JSON object with the following fields:
- `winnerTeamId` - UUID of the winning team as a string
- `result` - Result of the scrim (e.g., "2-1")
- `duration` - Duration of the scrim (e.g., "1h 30m")

**Response:**
- 200 OK - Updated Scrim object
- 400 Bad Request - If the request data is invalid or the scrim cannot be completed

### Find Active Scrims by Game

**GET /api/scrims/active/game/{gameId}**

Returns active scrims for a specific game.

**Path Parameters:**
- `gameId` - UUID of the game

**Response:** List of active Scrim objects for the specified game

### Find Pending Scrim Requests by Game

**GET /api/scrims/requests/pending/game/{gameId}**

Returns pending scrim requests for a specific game.

**Path Parameters:**
- `gameId` - UUID of the game

**Response:** List of pending ScrimRequest objects for the specified game

## Teams

### Get All Teams

**GET /api/teams**

Returns a list of all teams.

**Response:** List of Team objects

### Get Team by ID

**GET /api/teams/{id}**

Returns a specific team by ID.

**Path Parameters:**
- `id` - UUID of the team

**Response:**
- 200 OK - Team object
- 404 Not Found - If team with specified ID doesn't exist

### Create Team

**POST /api/teams**

Creates a new team.

**Request Body:** Team object

**Response:**
- 201 Created - Created Team object

### Update Team

**PUT /api/teams/{id}**

Updates an existing team.

**Path Parameters:**
- `id` - UUID of the team to update

**Request Body:** Team object

**Response:**
- 200 OK - Updated Team object
- 404 Not Found - If team with specified ID doesn't exist

### Delete Team

**DELETE /api/teams/{id}**

Deletes a team.

**Path Parameters:**
- `id` - UUID of the team to delete

**Response:**
- 204 No Content - If deletion was successful
- 404 Not Found - If team with specified ID doesn't exist

### Add Player to Team

**POST /api/teams/{id}/players**

Adds a player to a team.

**Path Parameters:**
- `id` - UUID of the team

**Request Body:** JSON object with the following fields:
- `playerId` - UUID of the player to add as a string

**Response:**
- 200 OK - Updated Team object
- 400 Bad Request - If the request data is invalid or the player cannot be added

### Remove Player from Team

**DELETE /api/teams/{teamId}/players/{playerId}**

Removes a player from a team.

**Path Parameters:**
- `teamId` - UUID of the team
- `playerId` - UUID of the player to remove

**Response:**
- 200 OK - Updated Team object
- 400 Bad Request - If the request data is invalid or the player cannot be removed

### Find Teams by Game

**GET /api/teams/game/{gameId}**

Returns teams that play a specific game.

**Path Parameters:**
- `gameId` - UUID of the game

**Response:** List of Team objects that play the specified game

### Find Available Teammates by Game

**GET /api/teams/game/{gameId}/players**

Returns available teammates for a specific game.

**Path Parameters:**
- `gameId` - UUID of the game

**Response:** List of PlayerProfile objects that play the specified game