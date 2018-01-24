# Contributing Guidelines

We love improvements to our tools! There are a few key ways you can help us improve our projects:

## First time contributors 

New to open source/free software? Here are some resources to get you started:

* http://www.firsttimersonly.com/
* http://www.charlotteis.co.uk/making-your-first-pull-request/
* A very in-depth guide: https://egghead.io/series/how-to-contribute-to-an-open-source-project-on-github
* https://guides.github.com/activities/contributing-to-open-source/
* On our [PublicLab.org GitHub repository](https://github.com/publiclab/plots2), we've listed some "good for first timers" bugs to fix here: https://github.com/publiclab/plots2/labels/first-timers-only
* We also have a slightly larger list of easy-ish but small and self contained issues: https://github.com/publiclab/plots2/labels/help-wanted


### Contributing for non-coders

As many of us are not coders but still play vital roles in advancing the project here are some basics for getting started, for those of us that are not coders.

Coding is not a requisite to being helpful on GitHub, if you don't code you can still help out; in fact, **helping to clearly describe and document problems and new feature proposals is at least as important as writing the code itself**.

When creating or editing an issue, try to:

1. Clearly describe the problem, linking to pages where it can be observed, or where a new feature might live. Include screenshots to be very specific!
2. (for bugs) If you don't know the problem, do what you can to help others narrow it down: provide contextual information like your browser, OS, and what you were doing when it happened. Did it used to work? Does it still, but only sometimes? Help them reproduce it!
3. Propose a solution. Whether or not you code, describing what **should** or **could** happen, or even what you expected to happen is always helpful to someone looking to fix it. This can be as simple as "It should show a notification." or "There should be a way to hide it."

Once an issue is well documented, we can tag it with `help-wanted` to get the word out that we're looking for someone to try to fix it. If you're not sure if it's ready, ask [on the plots-dev list](#Sign+up) 

Finally, if your issue is well documented, try to get involved in some outreach to new contributors to match someone with the project! Tell them what it'll help you achieve and why you'd appreciate help. And coordinate with the [plots-dev discussion list](#Sign+up) to get the word out.

### Preparing issues for newcomers

Related to the above, even if you are a coder, we need help "rolling out the red carpet" (as the [Hoodie project](http://hood.ie) calls it) for new contribtors, to grow our contributor base. The steps in [Contributing for non-coders](#Contributing+for+non-coders) are a good starting point, but as a coder, you can also deep-link to the relevant lines of code, with Github links and pointers like:

> Then the `:medium` in JavaScript on this line must be changed to `:large` too: https://github.com/publiclab/plots2/blob/master/app/assets/javascripts/dragdrop.js#L64

This is especially great for attracting coders who are not only new to our code, but new to coding in general! 

Learn more about how to make a good `first-timers-only` issue here:

https://publiclab.org/notes/warren/10-31-2016/create-a-welcoming-first-timers-only-issue-to-invite-new-software-contributors

Much of this post was adopted from @jywarren 's contribution to this wiki: https://publiclab.org/wiki/developers#First+time+contributors

### Submitting Feedback, Requests, and Bugs

Our process for submitting feedback, feature requests, and reporting bugs usually begins by submitting [GitHub issues](https://help.github.com/articles/about-issues/).

Some projects have additional templates or sets of questions for each issue, which you will be prompted to fill out when creating one.

### Submitting Code and Documentation Changes

Our process for accepting changes operates by [Pull Request (PR)](https://help.github.com/articles/about-pull-requests/) and has a few steps:

1.  If you haven't submitted anything before, and you aren't (yet!) a member of our organization, **fork and clone** the repo:

        $ git clone git@github.com:<your-username>/SmART-Form.git

    Organization members should clone the upsteam repo, instead of working from a personal fork:

        $ git clone git@github.com:publiclab/SmART-Form.git

1.  Create a **new branch** for the changes you want to work on. Choose a topic for your branch name that reflects the change:

        $ git checkout -b <branch-name>

1.  **Create or modify the files** with your changes. If you want to show other people work that isn't ready to merge in, commit your changes then create a pull request (PR) with _WIP_ or _Work In Progress_ in the title.

        https://github.com/publiclab/<repository-name>/pull/new/master

1.  Once your changes are ready for final review, commit your changes then modify or **create your pull request (PR)**, assign as a reviewer or ping (using "`@<username>`") a reop owner (someone able to merge in PRs) active on the project.

1.  Allow others sufficient **time for review and comments** before merging. We make use of GitHub's review feature to comment in-line on PRs when possible. There may be some fixes or adjustments you'll have to make based on feedback.

1.  Once you have integrated comments, or waited for feedback, an owner should merge your changes in!

_The final two sections of these guidelines are based on [EDGI](https://github.com/edgi-govdata-archiving/overview/blob/master/CONTRIBUTING.md)'s._
