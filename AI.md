AI Assistance Log
Tool Used

ChatGPT (OpenAI, GPT-5)

1. GUI Refactoring (DialogBox.java)

AI was used to:

Refactor DialogBox to support asymmetric message layout (user right, bot left).

Add CSS-based bubble styling (bubble-user, bubble-bot, bubble-error).

Implement circular avatar cropping using Circle clip.

Improve text wrapping and resizing behaviour.

AI helped:

Suggest a cleaner separation of formatting methods (formatAsUser, formatAsBot, formatAsError).

Improve UI responsiveness when resizing window.

Reduce excessive avatar size for better space optimisation.

All generated code was reviewed and manually adapted to fit project structure and naming conventions.

2. Welcome Message on Startup (MainWindow.java)

AI was used to:

Determine correct lifecycle placement of welcome message (in setShinchan() rather than initialize()).

Suggest logic to close window when bye is entered.

AI helped clarify:

JavaFX controller lifecycle

Proper injection timing for backend instance

3. Error Highlighting UX

AI suggested:

CSS classes for error bubble styling.

Prefixing error messages with a warning symbol.

Separation of UI logic from backend logic.

All AI-assisted code was:

Reviewed

Refactored for readability

Adjusted to conform to project coding standards