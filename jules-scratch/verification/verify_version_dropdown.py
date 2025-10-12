import time
from playwright.sync_api import sync_playwright, Error

def run(playwright):
    browser = playwright.chromium.launch(headless=True)
    context = browser.new_context()
    page = context.new_page()

    try:
        # Give the server a moment to start up
        time.sleep(5)

        page.goto("http://localhost:8080")

        # Click the "Get Started" button
        get_started_button = page.get_by_role("button", name="Get Started")
        get_started_button.click()

        # Click the version dropdown
        version_dropdown = page.get_by_role("button", name="MC 1.21")
        version_dropdown.click()

        # Take a screenshot
        page.screenshot(path="jules-scratch/verification/verification.png")

    except Error as e:
        print(f"An error occurred: {e}")
        print("Please ensure the development server is running on http://localhost:8080")
    finally:
        browser.close()

with sync_playwright() as playwright:
    run(playwright)