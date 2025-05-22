/**
 * Java implementation of Python's pickle serialization protocol.
 *
 * The {@link torch.pickle.Unpickler} supports the all pickle protocols.
 * The {@link torch.pickle.Pickler} supports most of the protocol (level 2 only though).
 *
 * Python's data types are mapped on their Java equivalents and vice versa.
 * Most basic data types and container types are supported by default.
 * You can add custom object pickle and unpickle classes to extend this
 * functionality.
 *
 * 
 * @version 1.5
 */
package torch.pickle

