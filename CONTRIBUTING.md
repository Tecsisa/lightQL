# Contribution policy

Contributions via GitHub pull requests are gladly accepted from their original author.
Along with any pull requests, please state that the contribution is your original work
and that you license the work to the project under the project's open source license.
Whether or not you state this explicitly, by submitting any copyrighted material via pull request,
email, or other means you agree to license the material under the project's open source license and
warrant that you have the legal authority to do so.

Please make sure to follow these conventions:

* For each contribution there must be a ticket (GitHub issue) with a short descriptive name, e.g. "Support for delegated functions"
* Work should happen in a branch named "ISSUE-DESCRIPTION", e.g. "32-functions-support"
* Before a PR can be merged, all commits must be squashed into one with its message made up from the ticket name and the ticket id, e.g. "Support for delegated functions (closes #32)"

## Contributing documentation

Contributions must include documentation when necessary, for instance if a new feature is added or an old one has been modified and requires an update.

### Source for the documentation

The documentation for this website is stored alongside the source, in the [docs](https://github.com/Tecsisa/lightQL/tree/master/docs).

### Generating the Site

run `sbt docs/makeMicrosite`

### Previewing the site

1. Install jekyll locally, depending on your platform, you might do this with:

    * yum install jekyll
    * apt-get install jekyll
    * gem install jekyll

2. In a shell, navigate to the generated site directory in `docs/target/site`

3. Start jekyll with `jekyll serve`

4. Navigate to http://localhost:4000/lightQL/ in your browser

5. Make changes to your site, and run `sbt docs/makeMicrosite` to regenerate the site. The changes should be reflected as soon as you run `makeMicrosite`.