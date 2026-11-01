/**
 * Moves account names out of _id: each account gets a generated _id and
 * stores its name in the name field. _id cannot be updated, so every
 * document is re-created. Safe to re-run: migrated documents are skipped.
 *
 * Run once against the account database, after a backup and before
 * deploying the version that adds the unique index on name.
 */

var migrated = 0;

db.accounts.find({ name: { $exists: false } }).toArray().forEach(function (account) {
    var name = account._id;
    var replacement = {};

    Object.keys(account).forEach(function (field) {
        if (field !== '_id') {
            replacement[field] = account[field];
        }
    });

    replacement._id = new ObjectId();
    replacement.name = name;

    var result = db.accounts.insert(replacement);
    if (result.hasWriteError()) {
        throw new Error('could not migrate account ' + name + ': ' + result.getWriteError().errmsg);
    }

    db.accounts.remove({ _id: name });
    migrated++;
});

print('accounts migrated: ' + migrated);
print('accounts without a name: ' + db.accounts.count({ name: { $exists: false } }));
