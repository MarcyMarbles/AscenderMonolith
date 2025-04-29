# Controller Refactoring Guide

This document provides instructions for implementing the refactored controllers and DTOs in the Ascender project.

## Overview of Changes

The following changes have been made to improve the codebase:

1. Created an abstract `BaseController` class with common functionality
2. Added DTOs for all REST requests and responses
3. Refactored controllers to extend the BaseController and use DTOs consistently
4. Removed duplicate code for user extraction and error handling

## New Files

### Base Controller

- `BaseController.java`: Abstract base controller with common functionality

### DTOs

- `PlayerProfileDTO.java`: DTO for PlayerProfile entity
- `ScrimDTO.java`: DTO for Scrim entity
- `ScrimRequestDTO.java`: DTO for ScrimRequest entity
- `ScrimRequestAcceptDTO.java`: DTO for accepting a scrim request
- `ScrimCompleteDTO.java`: DTO for completing a scrim
- `ScrimPlayerDTO.java`: DTO for adding a player to a scrim
- `TeamPlayerDTO.java`: DTO for adding or removing a player from a team
- `VotekickDTO.java`: DTO for initiating a votekick

### Refactored Controllers

- `ScrimControllerRefactored.java`: Refactored version of ScrimController
- `TeamControllerRefactored.java`: Refactored version of TeamController

## Implementation Steps

1. Add the new BaseController class
2. Add all the new DTO classes
3. Replace the existing controllers with the refactored versions:
   - Rename `ScrimControllerRefactored.java` to `ScrimController.java`
   - Rename `TeamControllerRefactored.java` to `TeamController.java`

4. Refactor other controllers in the project following the same pattern:
   - Extend BaseController
   - Use DTOs for all request and response objects
   - Remove duplicate code for user extraction
   - Add conversion methods between DTOs and entities

## Benefits

- **Improved Code Organization**: Common functionality is now in a base class
- **Better API Design**: DTOs provide a clear contract for API consumers
- **Reduced Duplication**: Common code is now in one place
- **Enhanced Security**: User extraction is standardized across controllers
- **Easier Maintenance**: Changes to common functionality only need to be made in one place