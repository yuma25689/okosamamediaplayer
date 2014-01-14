package okosama.app.media.ID3v2Frame;

import java.util.ArrayList;

public class FramesCollection<T> {
    private ArrayList<T> _Items; // Store All Data

    /// <summary>
    /// New Frames Collection
    /// </summary>
    FramesCollection()
    {
        _Items = new ArrayList<T>();
    }

    /// <summary>
    /// Add new item to list
    /// </summary>
    /// <param name="item"></param>
    public void Add(T item)
    {
        // Remove Item if Exists
        _Items.remove(item);

        _Items.add(item);
    }

    /// <summary>
    /// Remove Specific Item from list
    /// </summary>
    /// <param name="item">Item to remove</param>
    public void Remove(T item)
    {
        _Items.remove(item);
    }

    /// <summary>
    /// Remove at specific position
    /// </summary>
    /// <param name="index"></param>
    public void RemoveAt(int index)
    {
        _Items.remove(index);
    }

    /// <summary>
    /// Clear all
    /// </summary>
    public void Clear()
    {
        _Items.clear();
    }

    /// <summary>
    /// Array of items
    /// </summary>
//    public T[] getItems()
//    {
//    	T[] contents = null;//new T[_Items.size()];
//        return _Items.toArray(contents);
//    }

    /// <summary>
    /// Get sum of lengths of items
    /// </summary>
//    public int getTotalLength()
//    {
//        int Len = 0;
//        for (T item : _Items)
//        {
//            Len += item.Length;
//        }
//        return Len;
//    }

    /// <summary>
    /// Sort Items
    /// </summary>
//    public void Sort()
//    {
//        _Items.();
//    }

    /// <summary>
    /// Counts items of current FramesCollection
    /// </summary>
    public int getCount()
    {
    	return _Items.size();
    }

//    public IEnumerator GetEnumerator()
//    {
//        return _Items.GetEnumerator();
//    }

}
